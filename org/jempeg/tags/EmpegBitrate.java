package org.jempeg.tags;

public class EmpegBitrate {
	private StringBuffer myBitrateString;
	private boolean myHasVBR;
	private boolean myHasChannels;
	private boolean myHasBitsPerSecond;
	
	public EmpegBitrate() {
		myBitrateString = new StringBuffer();
	}

	public EmpegBitrate(String _str) {
		myBitrateString = new StringBuffer(_str);
	}

	public String toString() {
		return myBitrateString.toString();
	}
	
	public boolean hasVBR() {
		isVBR();
		return myHasVBR;
	}
	
	public boolean hasChannels() {
		getChannels();
		return myHasChannels;
	}
	
	public boolean hasBitsPerSecond() {
		getBitsPerSecond();
		return myHasBitsPerSecond;
	}

	public boolean isVBR() {
		myHasVBR = false;
		boolean vbr = false;
		if (myBitrateString.length() > 2) {
			switch (Character.toLowerCase(myBitrateString.charAt(0))) {
				case 'v' :
					myHasVBR = true;
					vbr = true;
					break;

				case 'f' :
					myHasVBR = true;
					vbr = false;
					break;
			}
		}
		return vbr;
	}

	public int getChannels() {
		myHasChannels = false;
		int channels = 0;
		if (myBitrateString.length() > 2) {
			switch (Character.toLowerCase(myBitrateString.charAt(1))) {
				case 'm' :
					myHasChannels = true;
					channels = 1;
					break;

				case 's' :
				myHasChannels = true;
					channels = 2;
					break;
			}
		}
		return channels;
	}

	public int getBitsPerSecond() {
		myHasBitsPerSecond = false;
		int bps = 0;
		if (myBitrateString.length() > 3) {
			try {
				bps = Integer.parseInt(myBitrateString.toString().substring(2)) * 1000;
				myHasBitsPerSecond = true;
			} catch (Throwable t) {
				bps = 0;
			}
		}
		return bps;
	}

	public void setVBR(boolean _vbr) {
		char v = (_vbr ? 'v' : 'f');
		if (myBitrateString.length() >= 1) {
			myBitrateString.setCharAt(0, v);
		} else {
			myBitrateString.append(v);
		}
	}

	public void setChannels(int _channels) {
		char c;
		switch (_channels) {
			case 1 :
				c = 'm';
				break;

			case 2 :
				c = 's';
				break;

			default :
				c = '?';
				break;
		}

		while (myBitrateString.length() < 2) {
			myBitrateString.append('?');
		}

		myBitrateString.setCharAt(1, c);
	}

	public void setBitsPerSecond(int _bps) {
		String b = String.valueOf(_bps / 1000);
		if (myBitrateString.length() < 2) {
			while (myBitrateString.length() < 2) {
				myBitrateString.append('?');
			}
		} else {
			myBitrateString.setLength(2);
		}

		myBitrateString.append(b);
	}
	
	/*
	public static void ASSERT(boolean _b) {
		if (!_b) {
			throw new RuntimeException("Failed.");
		}
	}
	
	public static void ASSERTEQ(boolean lhs, boolean rhs) {
		if (lhs != rhs) {
			throw new RuntimeException(lhs + " != " + rhs);
		}
	}
	
	public static void ASSERTEQ(int lhs, int rhs) {
		if (lhs != rhs) {
			throw new RuntimeException(lhs + " != " + rhs);
		}
	}

	public static void main(String[] _args) {
		EmpegBitrate br1 = new EmpegBitrate("fs128");
		ASSERT(br1.hasVBR());
		ASSERTEQ(br1.isVBR(), false);
		ASSERTEQ(br1.getChannels(), 2);
		ASSERTEQ(br1.getBitsPerSecond(), 128000);

		EmpegBitrate br2 = new EmpegBitrate("vm320");
		ASSERT(br2.hasVBR());
		ASSERT(br2.isVBR() == true);
		ASSERT(br2.getChannels() == 1);
		ASSERT(br2.getBitsPerSecond() == 320000);

		EmpegBitrate br3 = new EmpegBitrate("???");

		ASSERT(!br3.hasVBR());
		ASSERT(!br3.hasChannels());
		ASSERT(!br3.hasBitsPerSecond());

		EmpegBitrate br4 = new EmpegBitrate();
		ASSERT(!br4.hasVBR());
		ASSERT(!br4.hasChannels());
		ASSERT(!br4.hasBitsPerSecond());

		br1.setVBR(true);
		br1.setChannels(1);
		br1.setBitsPerSecond(64000);
		ASSERT(br1.isVBR() == true);
		ASSERTEQ(br1.getChannels(), 1);
		ASSERT(br1.getBitsPerSecond() == 64000);
		ASSERT(br1.toString().equals("vm64"));

		br3.setVBR(false);
		br3.setChannels(2);
		br3.setBitsPerSecond(192000);
		ASSERT(br3.isVBR() == false);
		ASSERT(br3.getChannels() == 2);
		ASSERT(br3.getBitsPerSecond() == 192000);
		ASSERT(br3.toString().equals("fs192"));

		br4.setBitsPerSecond(24000);
		br4.setVBR(true);
		br4.setChannels(1);
		ASSERT(br4.hasVBR());
		ASSERT(br4.isVBR() == true);
		ASSERT(br4.getChannels() == 1);
		ASSERT(br4.getBitsPerSecond() == 24000);
		ASSERT(br4.toString().equals("vm24"));

		EmpegBitrate br5 = new EmpegBitrate();
		br5.setBitsPerSecond(48000);
		br5.setChannels(2);
		br5.setVBR(false);
		ASSERT(br5.hasVBR());
		ASSERT(br5.isVBR() == false);
		ASSERT(br5.getChannels() == 2);
		ASSERT(br5.getBitsPerSecond() == 48000);
		ASSERT(br5.toString().equals("fs48"));

		EmpegBitrate br6 = new EmpegBitrate();
		br6.setBitsPerSecond(224000);
		br6.setChannels(2);
		br6.setVBR(true);
		ASSERT(br6.hasVBR());
		ASSERT(br6.isVBR() == true);
		ASSERT(br6.getChannels() == 2);
		ASSERT(br6.getBitsPerSecond() == 224000);
		ASSERT(br6.toString().equals("vs224"));
	}
	/**/
}
