package com.github.shnorbluk.telecharbanque;

public class PatternNotFoundException extends Exception
{
 private String pattern;
 public PatternNotFoundException(String pattern) {
  this.pattern=pattern;
 }
 public String getPattern() {
  return pattern;
 }
}
