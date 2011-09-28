package com.nfcpcdsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blogpost.hiro99ma.pcd.NfcF;
import com.blogpost.hiro99ma.pcd.NfcPcd;

public class MainActivity extends Activity implements com.blogpost.hiro99ma.pcd.UsbHost.UsbListener {
	private TextView mResultText = null;
	private com.blogpost.hiro99ma.pcd.UsbHost mUsbHost = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mUsbHost = new com.blogpost.hiro99ma.pcd.UsbHost();
		boolean ret = mUsbHost.onCreate(this);
		if(ret) {
			Button btn = (Button)findViewById(R.id.button_read);
			btn.setOnClickListener(mReadListener);
			mResultText = (TextView)findViewById(R.id.text_result);
		}
	}

	@Override
	public void onDestroy() {
		mUsbHost.onDestroy(this);
		super.onDestroy();
	}

	@Override
	public void inserted() {
		Toast.makeText(this, "inserted", Toast.LENGTH_LONG).show();
	}

	@Override
	public void removed() {
		Toast.makeText(this, "removed", Toast.LENGTH_LONG).show();
		finish();
	}

	private OnClickListener mReadListener = new OnClickListener() {
		public void onClick(View v) {
			final NfcPcd.NfcId nfcid = NfcPcd.getNfcId();

			StringBuffer sb = null;
			String title = "polling";
			String sub = "";
			boolean ret = NfcPcd.pollingF(NfcF.SYSCODE);
			boolean ret2 = false;
			if(!ret) {
				ret2 = NfcPcd.pollingF();
			}
			if(ret || ret2) {
				ret = true;
				sub = "(System Code:" + String.format("%04x", nfcid.SensRes & 0xffff) + ")";
			} else {
				ret = NfcPcd.pollingA();
			}
			if(ret) {
				//NfcIdは1つしかないので、ちょっと格好が悪いな
				title = nfcid.Label;
				byte len = nfcid.Length;
				sb = new StringBuffer(len * 2);
				sb.append(String.format("%02x", nfcid.Id[0] & 0xff));
				for(byte i=1; i<len; i++) {
					sb.append("-" + String.format("%02x", nfcid.Id[i] & 0xff));
				}
			} else {
				sb = new StringBuffer("fail.");
			}
			mResultText.setText(title + " : "+ sb + sub);
			NfcPcd.rfOff();
		}
	};

}