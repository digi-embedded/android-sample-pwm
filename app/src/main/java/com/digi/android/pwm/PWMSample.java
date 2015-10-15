/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.pwm;

import java.util.ArrayList;

import javax.sip.InvalidArgumentException;

import android.app.Activity;
import android.os.Bundle;
import android.pwm.PWM;
import android.pwm.PWMException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

/**
 * PWM sample application.
 *
 * <p>This example demonstrates the usage of the PWM API. User is allowed to configure
 * and control all the available PWM channels in the device.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class PWMSample extends Activity {

	// Constants.
	private static final String KERNEL_3_0 = "3.0";
	
	// Variables.
	private Switch enableButton;

	private RadioButton polarityNormalButton;
	private RadioButton polarityInvertedButton;

	private EditText frequencyText;
	private EditText dutyCycleText;

	private PWM pwmChannel;

	private Button applyFrequencyButton;
	private Button applyDutyCycleButton;
	
	private Spinner channelSelector;
	
	private ArrayList<String> channelSpinnerList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Initialize the application UI.
		initializeUI();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Disable the UI.
		enableUI(false);
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
				enableUI(false);
			}
		});
		enableButton = (Switch)findViewById(R.id.enable_button);
		enableButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleEnableButtonPressed();
			}
		});
		frequencyText = (EditText)findViewById(R.id.frequency_text);
		dutyCycleText = (EditText)findViewById(R.id.duty_text);
		polarityInvertedButton = (RadioButton)findViewById(R.id.polarity_inverted_button);
		polarityInvertedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handlePolarityInversedButtonPressed();
			}
		});
		polarityNormalButton = (RadioButton)findViewById(R.id.polarity_normal_button);
		polarityNormalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handlePolarityNormalButtonPressed();
			}
		});
		applyFrequencyButton = (Button)findViewById(R.id.apply_frequency_button);
		applyFrequencyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleApplyFrequencyButtonPressed();
			}
		});
		applyDutyCycleButton = (Button)findViewById(R.id.apply_duty_button);
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
		pwmChannel = new PWM(channel);
		updateValuesFromPWMChannel();
	}

	/**
	 * Updates the UI field values using the ones from the declared PWM channel.
	 * 
	 * @throws PWMException if there is an error reading PWM parameters. 
	 */
	private void updateValuesFromPWMChannel() throws PWMException {
		// Check enable status.
		boolean enabled = pwmChannel.isEnabled();
		if (enabled)
			enableButton.setChecked(true);
		else
			enableButton.setChecked(false);
		// Check frequency.
		double frequency = pwmChannel.getFrequency();
		frequencyText.setText(String.valueOf(frequency));
		// Check duty cycle.
		long dutyCycle = pwmChannel.getDutyCycle();
		dutyCycleText.setText(String.valueOf(dutyCycle));
		// Check polarity.
		int polarity = pwmChannel.getPolarity();
		switch (polarity) {
			case PWM.POLARITY_INVERSED:
				polarityInvertedButton.setChecked(true);
				polarityNormalButton.setChecked(false);
				break;
			case PWM.POLARITY_NORMAL:
				polarityNormalButton.setChecked(true);
				polarityInvertedButton.setChecked(false);
				break;
		}
		// Enable the UI.
		enableUI(true);
	}

	/**
	 * Handles what happens when the enable button is pressed.
	 */
	private void handleEnableButtonPressed() {
		if (enableButton.isChecked()) {
			if (!enablePWM())
				enableButton.setChecked(false);
		} else {
			if (!disablePWM())
				enableButton.setChecked(true);
		}
	}

	/**
	 * Enables the PWM channel.
	 * 
	 * @return True if success, false otherwise.
	 */
	private boolean enablePWM() {
		if (pwmChannel == null)
			return false;
		try {
			pwmChannel.enablePWM();
			return true;
		} catch (PWMException e) {
			showToast("Error enabling PWM channel: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Disables the PWM channel.
	 * 
	 * @return True if success, false otherwise.
	 */
	private boolean disablePWM() {
		if (pwmChannel == null)
			return false;
		try {
			pwmChannel.disablePWM();
			return true;
		} catch (PWMException e) {
			showToast("Error disabling PWM channel: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Handles what happens when the inverse polarity radio button is clicked.
	 */
	private void handlePolarityInversedButtonPressed() {
		if (polarityInvertedButton.isChecked()) {
			if (setPWMPolarity(PWM.POLARITY_INVERSED))
				polarityNormalButton.setChecked(false);
			else {
				polarityNormalButton.setChecked(true);
				polarityInvertedButton.setChecked(false);
			}
		} else {
			if (setPWMPolarity(PWM.POLARITY_NORMAL))
				polarityNormalButton.setChecked(true);
			else {
				polarityInvertedButton.setChecked(true);
				polarityNormalButton.setChecked(false);
			}
		}
	}
	
	/**
	 * Handles what happens when the normal polarity radio button is clicked.
	 */
	private void handlePolarityNormalButtonPressed() {
		if (polarityNormalButton.isChecked()) {
			if (setPWMPolarity(PWM.POLARITY_NORMAL))
				polarityInvertedButton.setChecked(false);
			else {
				polarityInvertedButton.setChecked(true);
				polarityNormalButton.setChecked(false);
			}
		} else {
			if (setPWMPolarity(PWM.POLARITY_INVERSED))
				polarityInvertedButton.setChecked(true);
			else {
				polarityInvertedButton.setChecked(false);
				polarityNormalButton.setChecked(true);
			}
		}
	}
	
	/**
	 * Changes the PWM channel polarity with the given one.
	 * 
	 * @param polarity New polarity to set.
	 * @return True if polarity was set correctly, false otherwise.
	 */
	private boolean setPWMPolarity(int polarity) {
		if (pwmChannel == null)
			return false;
		try {
			pwmChannel.setPolarity(polarity);
			return true;
		} catch (PWMException e) {
			showToast("Error changing PWM channel polarity: " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (InvalidArgumentException e) {
			showToast("Error changing PWM channel polarity: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Handles what happens when the apply frequency button is pressed.
	 */
	private void handleApplyFrequencyButtonPressed() {
		double frequency;
		if (frequencyText.getText().toString().length() == 0) {
			showToast("Frequency value cannot be empty");
			return;
		}
		try {
			frequency = Double.parseDouble(frequencyText.getText().toString());
			setPWMChannelFrequency(frequency);
		} catch (NumberFormatException e) {
			showToast("The specified frequency is not valid: " + frequencyText.getText());
		}
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
	 * Sets the PWM channel frequency.
	 * 
	 * @param frequency Frequency to set.
	 */
	private void setPWMChannelFrequency(double frequency) {
		if (pwmChannel == null)
			return;
		try {
			pwmChannel.setFrequency(frequency);
		} catch (InvalidArgumentException e) {
			showToast("Error setting PWM channel frequency: " + e.getMessage());
			e.printStackTrace();
		} catch (PWMException e) {
			showToast("Error setting PWM channel frequency: " + e.getMessage());
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
		} catch (InvalidArgumentException e) {
			showToast("Error setting PWM channel duty cycle: " + e.getMessage());
			e.printStackTrace();
		} catch (PWMException e) {
			showToast("Error setting PWM channel duty cycle: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Changes the enable status of the UI.
	 * 
	 * @param enable True to enable UI, false to disable.
	 */
	private void enableUI(boolean enable) {
		// Kernel 3.0 version and SBCs do not allow to modify several of the PWM parameters.
		String kernelVersion = BoardUtils.getKernelVersion();
		boolean supported = true;
		if ((kernelVersion != null && kernelVersion.startsWith(KERNEL_3_0)) || BoardUtils.isMX6SBC()) {
			supported = false;
			(findViewById(R.id.polarity_tview)).setVisibility(View.INVISIBLE);
			polarityInvertedButton.setVisibility(View.INVISIBLE);
			polarityNormalButton.setVisibility(View.INVISIBLE);
		}
		enableButton.setEnabled(enable && supported);
		frequencyText.setEnabled(enable && supported);
		dutyCycleText.setEnabled(enable);
		polarityInvertedButton.setEnabled(enable && supported);
		polarityNormalButton.setEnabled(enable && supported);
		applyFrequencyButton.setEnabled(enable && supported);
		applyDutyCycleButton.setEnabled(enable);
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
		channelSpinnerList = new ArrayList<String>();
		// Read available channels and store them in the array.
		int[] availableChannels = PWM.listAvilableChannels();
		for (int availableChannel : availableChannels) {
			channelSpinnerList.add("" + availableChannel);
		}
		// Create an array adapter using our channels array.
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, channelSpinnerList);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view.
		// Set the array adapter to the PWM channels spinner.
		channelSelector.setAdapter(spinnerArrayAdapter);
	}
}