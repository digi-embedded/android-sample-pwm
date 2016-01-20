/**
 * Copyright (c) 2014-2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.sample.pwm;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.digi.android.pwm.PWM;
import com.digi.android.pwm.PWMException;
import com.digi.android.pwm.PWMManager;

/**
 * PWM sample application.
 *
 * <p>This example demonstrates the usage of the PWM API. User is allowed to configure
 * and control all the available PWM channels in the device.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class PWMSampleActivity extends Activity {
	
	// Variables.
	private EditText dutyCycleText;

	private PWMManager pwmManager;

	private PWM pwmChannel;

	private Spinner channelSelector;
	
	private ArrayList<String> channelSpinnerList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		pwmManager = new PWMManager(this);
		// Initialize the application UI.
		initializeUI();
		// Hide keyboard.
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Check if the PWM channel is declared.
		if (pwmChannel == null) {
			try {
				// Initialize PWM channel.
				initializePWMChannel();
			} catch (PWMException e) {
				showToast("Error initializing PWM channel: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			try {
				// Update values from the PWM channel.
				updateValuesFromPWMChannel();
			} catch (PWMException e) {
				showToast("Error updating UI PWM channel values: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initializes the UI components of the application.
	 */
	private void initializeUI() {
		channelSelector = (Spinner)findViewById(R.id.channel_selector);
		// Fill the spinner values.
		fillPWMChannels();
		channelSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				handlePWMChannelSelection();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		dutyCycleText = (EditText)findViewById(R.id.duty_text);
		Button applyDutyCycleButton = (Button) findViewById(R.id.apply_duty_button);
		applyDutyCycleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleApplyDutyCycleButtonPressed();
			}
		});
	}

	/**
	 * Initializes the PWM channel.
	 * 
	 * @throws PWMException if there was an error initializing the PWM channel.
	 */
	private void initializePWMChannel() throws PWMException {
		if (channelSpinnerList == null || channelSpinnerList.size() == 0)
			return;
		int channel = Integer.valueOf(channelSpinnerList.get(channelSelector.getSelectedItemPosition()));
		pwmChannel = pwmManager.createPWM(channel);
		updateValuesFromPWMChannel();
	}

	/**
	 * Updates the UI field values using the ones from the declared PWM channel.
	 * 
	 * @throws PWMException if there is an error reading PWM parameters. 
	 */
	private void updateValuesFromPWMChannel() throws PWMException {
		// Check duty cycle.
		double dutyCycle = pwmChannel.getDutyCycle();
		dutyCycleText.setText(String.valueOf((int)dutyCycle));
	}
	
	/**
	 * Handles what happens when the apply duty cycle button is pressed.
	 */
	private void handleApplyDutyCycleButtonPressed() {
		int dutyCycle ;
		if (dutyCycleText.getText().toString().length() == 0) {
			showToast("Duty cycle value cannot be empty");
			return;
		}
		try {
			dutyCycle = Integer.parseInt(dutyCycleText.getText().toString());
			setPWMChannelDutyCycle(dutyCycle);
		} catch (NumberFormatException e) {
			showToast("The specified duty cycle is not valid: " + dutyCycleText.getText());
		}
	}
	
	/**
	 * Handles what happens when a PWM Channel is selected.
	 */
	private void handlePWMChannelSelection() {
		try {
			initializePWMChannel();
		} catch (PWMException e) {
			showToast("Error initializing PWM channel: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the PWM channel duty cycle.
	 * 
	 * @param dutyCycle The Duty cycle to set.
	 */
	private void setPWMChannelDutyCycle(int dutyCycle) {
		if (pwmChannel == null)
			return;
		try {
			pwmChannel.setDutyCycle(dutyCycle);
		} catch (IllegalArgumentException | PWMException e) {
			showToast("Error setting PWM channel duty cycle: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows a toast with the given message.
	 * 
	 * @param message Message to show.
	 */
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Fills the channel spinner with the available PWM channels.
	 */
	private void fillPWMChannels() {
		// Initialize array to store elements.
		channelSpinnerList = new ArrayList<>();
		// Read available channels and store them in the array.
		int[] availableChannels = pwmManager.listChannels();
		for (int availableChannel : availableChannels) {
			channelSpinnerList.add(String.valueOf(availableChannel));
		}
		// Create an array adapter using our channels array.
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, channelSpinnerList);
		// Set the array adapter to the PWM channels spinner.
		channelSelector.setAdapter(spinnerArrayAdapter);
	}
}