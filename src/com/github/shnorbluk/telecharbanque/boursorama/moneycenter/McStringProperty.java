package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.orm.MoneycenterProperty;

public abstract class McStringProperty<OPE extends MoneycenterOperation> extends MoneycenterProperty<String> 
{
	public McStringProperty(String name, String constraint) {
		super(name, "TEXT", constraint);
	}
		protected abstract void setValue(McOperationInDb ope, String val);
		
}
