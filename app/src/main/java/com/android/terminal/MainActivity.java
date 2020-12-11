package com.android.terminal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.text.*;
import java.io.*;
import android.os.*;
import android.widget.*;

public class MainActivity extends Activity
{
    private SharedPreferences mPreferences;
    private boolean isMono;
    private Uri data = null;
    private TextView contentView;
    private Button btn;
    private PopupMenu popup;
	private static Run run;
	static String cmd;
	static boolean flag;
	String fn;
	int x;

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = getPreferences(MODE_PRIVATE);

        contentView = (TextView) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.optnBtn);
        popup = new PopupMenu(MainActivity.this, btn);
		font(20);
        float fontSize = mPreferences.getFloat(App.PREF_Size, App.DEFAULT_Size);
        isMono = mPreferences.getBoolean(App.PREF_Mono, App.DEFAULT_Mono);
		isMono = false;
        contentView.setTextSize(fontSize);
        if (isMono == true)
		{
            contentView.setTypeface(Typeface.MONOSPACE);
        }
		else
		{
            contentView.setTypeface(Typeface.SANS_SERIF);
        }

        popup.dismiss();
        popup.getMenuInflater().inflate(R.menu.fontsel, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item)
				{
					float fontSize = mPreferences.getFloat(App.PREF_Size, App.DEFAULT_Size);
					switch (item.getItemId())
					{
						case R.id.smallerSize:
							fontSize = fontSize * 0.8f;
							break;
						case R.id.biggerSize:
							fontSize = fontSize * 1.2f;
							break;
						case R.id.monoStyle:
							if (item.isChecked() == false)
							{
								item.setChecked(true);
								isMono = true;
								contentView.setTypeface(Typeface.MONOSPACE);
							}
							else
							{
								item.setChecked(false);
								isMono = false;
								contentView.setTypeface(Typeface.SANS_SERIF);
							}
							break;
						default:
							return false;
					}
					contentView.setTextSize(fontSize);
					SharedPreferences.Editor editor = mPreferences.edit();
					editor.putFloat(App.PREF_Size, fontSize);
					editor.putBoolean(App.PREF_Mono, isMono);
					editor.apply();
					return true;
				}
			});

        btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					//MenuItem mi = popup.getMenu().findItem(R.id.monoStyle);
					//mi.setChecked(isMono);
					//popup.show();
					run();
				}
			});

        Intent intent = getIntent();
        data = intent.getData();
        if (data != null)
		{
            try
			{
                InputStream input = getContentResolver().openInputStream(data);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

                String text = "";
                while (bufferedReader.ready())
				{
                    text += bufferedReader.readLine() + "\n";
                }
                contentView.setText(text);
            }
			catch (Exception e)
			{
                e.printStackTrace();
            }
        }
		else
		{
            File file = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
				App.PACKAGE_NAME
            );

            String path = file.getPath() + App.NOTE_FILENAME;
			fn = path;
            try
			{
                file.mkdirs();
                file = new File(path);
                if (!file.exists()) file.createNewFile();
                data = Uri.fromFile(file);
            }
			catch (Exception e)
			{
                Toast.makeText(
					getApplicationContext(),
					getString(R.string.errAccess) + "\n" + data.getPath(),
					Toast.LENGTH_LONG
                ).show();
            }
        }
		read();
    }

    @Override
    protected void onPause()
	{
        super.onPause();
		if (!flag)
		{
			if (data != null)
			{
				//write();
			}
		}
    }

    @Override
    protected void onResume()
	{
        super.onResume();
		if (!flag)
		{
			if (data != null)
			{
				//read();
			}
		}
    }

	public void font(int fontSize)
	{
		contentView.setTextSize(fontSize);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putFloat(App.PREF_Size, fontSize);
		editor.putBoolean(App.PREF_Mono, isMono);
		editor.apply();
	}

	public void reset()
	{
		contentView.append("\n");
		cursorToEnd();
		flag = true;
	}

	public void run()
	{
		if (!flag)
		{
			flag = true;
			cmd = contentView.getText().toString();
			write(cmd);
			cls();
			if (run != null)
			{
				run.p.destroy();
			}
			run = new Run(this);
			run.start();
		}
		else
		{
			if (run != null)
			{
				run.p.destroy();
				run = null;
			}
			flag = false;
			read();
		}
	}

	public void read()
	{
		try
		{
			File file = new File(data.getPath());

			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file))
			);
			String text = "";
			while (bufferedReader.ready())
			{
				text += bufferedReader.readLine() + "\n";
			}
			contentView.setText(text);
			cursorToEnd();
		}
		catch (Exception e)
		{
			Toast.makeText(
				getApplicationContext(),
				getString(R.string.errRead) + "\n" + data.getPath(),
				Toast.LENGTH_LONG
			).show();
		}
	}

	public void write(String s)
	{
		try
		{
			File file = new File(data.getPath());
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(s.getBytes());
			fos.flush();
			fos.close();
		}
		catch (Exception e)
		{
			Toast.makeText(
				getApplicationContext(),
				getString(R.string.errWrite) + "\n" + data.getPath(),
				Toast.LENGTH_LONG
			).show();
		}
	}

	public void cursorToEnd()
	{
		Editable edit=contentView.getEditableText();
		Selection.setSelection(edit, edit.length());
	}

	public Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{

			String result=(String)msg.obj;
			if (result.startsWith("clear") || result.startsWith("cls"))
			{
				cls();
				result = "";
			}
			if (flag)
			{
				contentView.append(result);
				scrollDown();
				x++;
				if (x > 500)
				{
					cls();
				}
			}
		}
	};

	public void scrollDown()
	{
		final ScrollView esv = (ScrollView)findViewById(R.id.sv);
		esv.post(new Runnable()
			{
				public void run()
				{
					esv.fullScroll(ScrollView.FOCUS_DOWN);
				}
			}
		);
		//esv.setFocusable(false);
	}

	public void cls()
	{
		x = 0;
		contentView.setText("");
		cursorToEnd();
	}

	public void exec(String s)
	{
		run.input(s);
	}

	class Run extends Thread implements Runnable
	{
		MainActivity ma;
		java.lang.Process p;
		InputStream out;
		InputStream err;
		OutputStream in;
		boolean done;

		Run(MainActivity ma)
		{
			try
			{
				this.ma = ma;
				p = Runtime.getRuntime().exec("/system/bin/sh " + ma.fn);
				in = p.getOutputStream();
				out = p.getInputStream();
				err = p.getErrorStream();
			}
			catch (Exception e)
			{}
		}

		public void run()
		{
			try
			{
				while (true)
				{
					dump();
					pause(100);
				}
			}
			catch (Exception e)
			{}
		}

		public void dump()
		{
			try
			{
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				if (ma.flag)
				{
					if (out.available() > 0)
					{
						int i=0;
						while ((i = out.read()) != -1)
						{
							baos.write(i);
							if (i == 10)
							{
								print(new String(baos.toByteArray()));
								baos = new ByteArrayOutputStream();
							}
						}
					}
					if (err.available() > 0)
					{
						int i=0;
						while ((i = err.read()) != -1)
						{
							baos.write(i);
							if (i == 10)
							{
								print(new String(baos.toByteArray()));
								baos = new ByteArrayOutputStream();
							}
						}
					}
					print(new String(baos.toByteArray()));
				}
			}
			catch (Exception e)
			{}
		}

		public void input(String s)
		{
			try
			{
				in.write((s + "\n").getBytes());
				in.flush();
			}
			catch (Exception e)
			{}
		}

		public void print(String s)
		{
			ma.handler.sendMessage(Message.obtain(ma.handler, 0, s));
			pause(10);
		}

		public void pause(long time)
		{
			try
			{
				sleep(time);
			}
			catch (InterruptedException e)
			{}
		}

		public boolean done()
		{
			new Thread(){
				public void run()
				{
					try
					{
						p.waitFor();
						done = true;
					}
					catch (InterruptedException e)
					{}
				}
			}.start();
			return done;
		}
	}
}
