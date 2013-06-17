package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import java.util.*;

public class AttributesOperationChange extends OperationChange
{
	private final List<MCPropertyValue> propertiesToSet;
	private boolean done=false;
	public boolean isDone() {
		return done;
	}
	
	@Override
	public void perform (MoneycenterPersistence persistence) {
		//TODO
		done=true;
	}
	public AttributesOperationChange(String id, List<MCPropertyValue> propertiesToSet) {
		super(id);
		this.propertiesToSet=propertiesToSet;
	}
}
