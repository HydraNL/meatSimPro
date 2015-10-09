package main;

import framework.SocialPractice;
import repast.simphony.parameter.StringConverter;

public class SocialPracticeConverter implements StringConverter<SocialPractice> {

	@Override
	public String toString(SocialPractice obj) {
		return obj.toString();
	}

	@Override
	public SocialPractice fromString(String strRep) {
		return null;
	}

}
