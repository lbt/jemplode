package org.jempeg.empeg.protocol;

import com.inzyme.progress.ISimpleProgressListener;

public class PingProgressListener implements ISimpleProgressListener {
	private ISimpleProgressListener myProxiedProgressListener;
	private Pinger myPinger;
  
	public PingProgressListener(EmpegProtocolClient _protocolClient, ISimpleProgressListener _proxiedProgressListener) {
    myPinger = new Pinger(_protocolClient);
		myProxiedProgressListener = _proxiedProgressListener;
	}

	public void progressReported(long _current, long _maximum) {
    myPinger.pingIfNecessary();
		myProxiedProgressListener.progressReported(_current, _maximum);
	}

	public void progressReported(String _description, long _current, long _maximum) {
    myPinger.pingIfNecessary();
		myProxiedProgressListener.progressReported(_description, _current, _maximum);
	}
}
