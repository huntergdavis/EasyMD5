package com.hunterdavis.easymd5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class EasyMDFive extends Activity {

	int SELECT_FILE = 122;

	String filePath = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Create an anonymous implementation of OnClickListener
		OnClickListener loadButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				// in onCreate or any event where your want the user to
				Intent intent = new Intent(v.getContext(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/sdcard");
				startActivityForResult(intent, SELECT_FILE);

			}
		};

		// Create an anonymous implementation of OnClickListener
		OnClickListener saveButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				saveMD5File();

			}
		};

		Button loadButton = (Button) findViewById(R.id.loadButton);
		loadButton.setOnClickListener(loadButtonListner);

		Button saveButton = (Button) findViewById(R.id.savebutton);
		saveButton.setOnClickListener(saveButtonListner);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());

	}

	public void saveMD5File() {
		String md5FileName = filePath + ".MD5";
		EditText t = (EditText) findViewById(R.id.mdfive); 
		String md5ActualText = t.getText().toString();
		String fileNameString = getFileName();
		String fileString = md5ActualText + " *" + fileNameString;

		OutputStream os = null;

		// now try to open the first output file
		try {
			os = new FileOutputStream(md5FileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			os.write(fileString.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView dt = (TextView) findViewById(R.id.md5fileText);
		dt.setText("MD5 File Matches!");
		
		Toast.makeText(getBaseContext(), "Saved " + md5FileName,
				Toast.LENGTH_SHORT).show();

	}

	public String getFileName() {
		int slashloc = filePath.lastIndexOf("/");
		if (slashloc < 0) {
			return filePath;
		} else {
			return filePath.substring(slashloc+1);
		}
	}

	public void checkForMd5FileAndSetText() {
		String md5FileName = filePath + ".MD5";
		EditText t = (EditText) findViewById(R.id.mdfive);
		TextView md5FileText = (TextView) findViewById(R.id.md5fileText);
		String md5ActualText = t.getText().toString();
		String fileNameString = getFileName();
		String fileString = md5ActualText + " *" + fileNameString;
		
		String readText = "";
		
		
		InputStream is = null;
		try {
			is = new FileInputStream(md5FileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		try {
			while((readText = r.readLine()) != null) {
				if(readText.startsWith("#"))
				{
					// ignore this line
					md5FileText.setText("MD5 File Does Not Match!"+readText);
				}
				else if(readText.length() < 10)
				{
					// really can't have less than 10 chars
					md5FileText.setText("MD5 File Does Not Match!"+readText);
				}
				else
				{
					if(readText.equalsIgnoreCase(fileString)) {
						md5FileText.setText("MD5 File Matches!");
					}
					else {
						md5FileText.setText("MD5 File Does Not Match!"+readText);
					}
				}
						
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	public void onActivityResult(final int requestCode, int resultCode,
			final Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				filePath = data.getStringExtra(FileDialog.RESULT_PATH);
				// set the filename txt
				changeFileNameText(filePath);
				// md5 the file
				String md5String = md5(filePath);
				changeMD5Text(md5String);
				Button enabButton = (Button) findViewById(R.id.savebutton);
				enabButton.setEnabled(true);

				// check for .md5 file and set md5 file text
				checkForMd5FileAndSetText();

			}
		} else if (resultCode == RESULT_CANCELED) {
		}
	}

	public void changeFileNameText(String newFileName) {
		TextView t = (TextView) findViewById(R.id.fileText);
		t.setText(newFileName);
	}

	public void changeMD5Text(String newMD5) {
		EditText t = (EditText) findViewById(R.id.mdfive);
		t.setText(newMD5);
	}

	public static String md5(String fileString) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			InputStream is = null;
			try {
				is = new FileInputStream(fileString);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] buffer = new byte[8 * 1024];
			int read;
			try {
				while ((read = is.read(buffer)) > 0) {
					digest.update(buffer, 0, read);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}