package com.github.shnorbluk.telecharbanque.boursorama;

//import android.graphics.*;
//import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.android.MainActivity;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.ui.MessageObserver;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;

import javax.imageio.stream.FileImageInputStream;

import org.apache.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoursoramaClient extends SessionedBufferedHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoursoramaClient.class);

	private final BufferedHttpClient hClient;
	private boolean connected = false;
	private final String username;
	private final String password;

	public BoursoramaClient(final BufferedHttpClient hClient, String username, String password) {
		super(hClient);
		this.hClient = hClient;
		this.username = username;
		this.password = password;
	}

	public int getSessionInformation() {
		return -1;
	}

//	private void displayMessage(String msg, boolean persistent) {
	//	for (final MessageObserver observer:observers){
		//	observer.displayMessage(msg, persistent);
//		}
	//}
	public boolean isConnected() {
		return connected;
	}

	// void setSimulationMode ( boolean simuMode) {
	// hclient = new FakeHClient(currentTask);
	// hClient.setSimulationMode(simuMode);
	// simu = true;
	// }

	private static final int DIGIT_WIDTH = 100;
	private static final int DIGIT_HEIGHT = 60;
	private static final int FIRST_SIGNIFICANT_ROW = 0;
	private static final int LAST_SIGNIFICANT_ROW = 59;

	private String[] decodePad(String filepath) throws IOException {
		final PlatformSpecific util = PlatformSpecific.getInstance();
		Bitmap mBitmapKeyboard = util.loadBitmap(filepath);
		String[] map = new String[10];
		//int[] keybPixels = new int[DIGIT_WIDTH * DIGIT_HEIGHT];
		for (int yKeyb = 0; yKeyb <= 3 * DIGIT_HEIGHT; yKeyb += DIGIT_HEIGHT) {
			for (int xKeyb = 0; xKeyb <= 2 * DIGIT_WIDTH; xKeyb += DIGIT_WIDTH) {
				displayMessage("Décodage du chiffre à la position " + xKeyb + "," + yKeyb, false);
				LOGGER.info("{},{}", xKeyb, yKeyb);
				int[] keybPixels = mBitmapKeyboard.getPixels(xKeyb, yKeyb, DIGIT_WIDTH, DIGIT_HEIGHT);
				int minDiff = Integer.MAX_VALUE;
				int mostAccurate = -1;
				String diffs = "";
				for (int digit = 0; digit <= 10; digit++) {
					Bitmap digitBm = util.loadBitmap(util.loadResource(digit + ".png"));
					int[] digitPixels = digitBm.getPixels(0, 0, DIGIT_WIDTH, DIGIT_HEIGHT);
					int diff = 0;
					for (int pixIndex = FIRST_SIGNIFICANT_ROW * DIGIT_WIDTH; pixIndex < LAST_SIGNIFICANT_ROW
							* DIGIT_WIDTH; pixIndex++) {
						int pix = keybPixels[pixIndex];
						int digitPix = digitPixels[pixIndex];
						int rpix = util.redPartOfColor(pix);
						int rdigitPix = util.redPartOfColor(digitPix);
						if ((rpix >= 238 && rpix <= 248) != (rdigitPix >= 238 && rdigitPix <= 247)) {
							diff++;
						}
					}
					diffs += diff + " ";
					if (diff < minDiff) {
						minDiff = diff;
						mostAccurate = digit;
					}
				}
				LOGGER.info("{},{}:{} {}", new Object[]{xKeyb, yKeyb, mostAccurate, diffs});
				if (mostAccurate != 10) {
					map[mostAccurate] = xKeyb + "," + yKeyb;
				}
			}
		}
		return map;
	}

	@Override
	protected void connect() throws ConnectionException {
		LOGGER.debug("Connexion ");
		boolean online = true;
		if (password == null) {
			throw new ConnectionException("Le mot de passe n'a pas ete saisi.");
		}
		displayMessage("Connexion à Boursorama  en cours", true);
		try {
			StringBuffer connectionPage = hClient.loadString("https://www.boursorama.com/connexion.phtml?", null, true,
					"");
			String connectionPageStr = connectionPage.toString();
			String imgUrl = Utils.findGroupAfterPattern(connectionPageStr, "<img id=\"login-pad_pad", "src=\"([^\"]*)");
			imgUrl = "https://www.boursorama.com" + imgUrl;
			String filePath = MoneycenterPersistence.TEMP_DIR + "/boursopad.gif";
			if (online) {
				hClient.httpget(imgUrl).save(filePath);
			}
			String[] coordMap = decodePad(filePath);
			String[] parts = connectionPageStr.split("<area shape");
			HashMap<String, String> codeMap = new HashMap<String, String>(12);
			for (int i = 1; i < parts.length; i++) {
				String coord = Utils.findGroupAfterPattern(parts[i], "coords=", "\"([0-9]+,[0-9]+)");
				String code = Utils.findGroupAfterPattern(parts[i], "\\+= '", "(....|)'");

				codeMap.put(coord, code);
			}
			LOGGER.debug("codeMap={}", codeMap);
			String encoded = "";
			for (char digitChar : password.toCharArray()) {
				int digit = Character.digit(digitChar, 10);
				String digitCode = codeMap.get(coordMap[digit]);
				encoded += digitCode;
			}
			String[] params = new String[] { "login", username, "password", encoded,
					"password_fake", "*******", "submit2", "Se+Connecter", "is_first", "0" };
			String uri = "https://www.boursorama.com/logunique.phtml";
			StringBuffer page = hClient.loadString(uri, params, true, "");
			if (page.indexOf("Identifiant ou mot de passe incorrect") > 0) {
				throw new ConnectionException("Identifiant ou mot de passe incorrect");
			}
		} catch (IOException e) {
			throw new ConnectionException(e);
		} catch (PatternNotFoundException e) {
			throw new ConnectionException(e);
		}

		connected = true;
		displayMessage("Connecté", true);

	}

}
