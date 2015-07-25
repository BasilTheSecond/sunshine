package com.example.android.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class
MainActivity extends ActionBarActivity
{
	@Override
	protected void
	onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new ForecastFragment())
				.commit();
		}
	}

	@Override
	public boolean
	onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean
	onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.action_settings:
				Toast.makeText(this, R.string.action_settings, Toast.LENGTH_SHORT).show();
				return true;
			case R.id.action_help:
				Toast.makeText(this, R.string.action_help, Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
