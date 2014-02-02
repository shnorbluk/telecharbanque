package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;
import org.orman.mapper.*;
import org.orman.mapper.annotation.*;

import org.orman.mapper.annotation.Entity;

@Entity
public class MCPropertyValue extends Model<MCPropertyValue>
{
@ManyToOne
public AttributesOperationChange change;
}
