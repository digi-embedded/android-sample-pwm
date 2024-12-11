/*
 * Copyright (c) 2014-2025, Digi International Inc. <support@digi.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.digi.android.sample.pwm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.digi.android.pwm.PWM;
import com.digi.android.pwm.PWMChip;
import com.digi.android.pwm.PWMException;
import com.digi.android.pwm.PWMManager;
import com.digi.android.pwm.PWMPolarity;

/**
 * PWM sample application.
 *
 * <p>This example demonstrates the usage of the PWM API. You can configure
 * and control all the available PWM channels in the device.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class PWMSampleActivity extends Activity {

	// Constants.
	private final static String CCIMX6SBC_NAME = "ccimx6sbc";

	// Variables.
	private Switch enableButton;

	private RadioButton polarityNormalButton;
	private RadioButton polarityInvertedButton;

	private EditText frequencyText;
	private EditText dutyCycleText;

	private PWMManager pwmManager;

	private PWM pwmChannel;

	private Spinner chipSelector;
	private Spinner channelSelector;
	
	private ArrayList<PWMChip> chipsList;

	private ArrayList<Integer> channelsList;

	private ArrayAdapter<PWMChip> chipsAdapter;

	private ArrayAdapter<Integer> channelsAdapter;

	private PWMChip selectedChip = null;

	private Integer selectedChannel = null;

	private Button applyFrequencyButton;
	private Button applyDutyCycleButton;

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
		// Chips spinner.
		chipsList = new ArrayList<>();
		chipsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, chipsList);
		chipSelector = findViewById(R.id.chip_selector);
		chipSelector.setAdapter(chipsAdapter);
		chipSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedChip = (PWMChip)chipSelector.getSelectedItem();
				updateChannels();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				channelsAdapter.clear();
				channelsAdapter.notifyDataSetChanged();
				selectedChip = null;
				selectedChannel = null;
			}
		});
		// Channels spinner.
		channelsList = new ArrayList<>();
		channelsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, channelsList);
		channelSelector = findViewById(R.id.channel_selector);
		channelSelector.setAdapter(channelsAdapter);
		channelSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				handleChannelSelectionChanged();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				selectedChannel = null;
				enableUI(false);
			}
		});
		// Enable button.
		enableButton = findViewById(R.id.enable_button);
		enableButton.setOnClickListener(v -> handleEnableButtonPressed());
		// Polarity.
		polarityInvertedButton = findViewById(R.id.polarity_inverted_button);
		polarityInvertedButton.setOnClickListener(v -> {
			if (polarityInvertedButton.isChecked())
				handlePolarityChanged(PWMPolarity.INVERSED);
		});
		polarityNormalButton = findViewById(R.id.polarity_normal_button);
		polarityNormalButton.setOnClickListener(v -> {
			if (polarityNormalButton.isChecked())
				handlePolarityChanged(PWMPolarity.NORMAL);
		});
		// Frequency.
		frequencyText = findViewById(R.id.frequency_text);
		// Duty cycle.
		dutyCycleText = findViewById(R.id.duty_text);
		// Apply frequency button.
		applyFrequencyButton = findViewById(R.id.apply_frequency_button);
		applyFrequencyButton.setOnClickListener(v -> handleApplyFrequencyButtonPressed());
		// Apply duty cycle button.
		applyDutyCycleButton = findViewById(R.id.apply_duty_button);
		applyDutyCycleButton.setOnClickListener(v -> handleApplyDutyCycleButtonPressed());
		// Fill the chip values (will trigger channel values fill).
		fillPWMChips();
	}

	/**
	 * Fills the chips spinner with the available PWM chips.
	 */
	private void fillPWMChips() {
		chipsList.clear();
		// Read available chips and store them in the array.
		chipsList.addAll(pwmManager.listPWMChips());
		chipsList.sort(Comparator.comparing(PWMChip::getName));
		chipsAdapter.notifyDataSetChanged();
		if (!chipsList.isEmpty())
			chipSelector.setSelection(0);
		else
			chipSelector.setSelection(-1);
	}

	/**
	 * Updates the channels list depending on the selected chip.
	 */
	private void updateChannels() {
		channelsList.clear();
		List<Integer> channels = selectedChip.listChannels();
		channelsList.addAll(channels);
		Collections.sort(channelsList);
		channelsAdapter.notifyDataSetChanged();
		if (!channelsList.isEmpty())
			channelSelector.setSelection(0);
		else
			channelSelector.setSelection(-1);
		handleChannelSelectionChanged();
	}

	/**
	 * Initializes the PWM channel.
	 * 
	 * @throws PWMException if there was an error initializing the PWM channel.
	 */
	private void initializePWMChannel() throws PWMException {
		if (selectedChip == null || selectedChannel == null)
			return;

		pwmChannel = pwmManager.createPWM(selectedChip, selectedChannel);
		updateValuesFromPWMChannel();
	}

	/**
	 * Updates the UI field values using the ones from the declared PWM channel.
	 * 
	 * @throws PWMException if there is an error reading PWM parameters. 
	 */
	private void updateValuesFromPWMChannel() throws PWMException {

		// Duty cycle
		dutyCycleText.setText(String.valueOf(pwmChannel.getDutyCyclePercentage()));
		if (!Build.DEVICE.equals(CCIMX6SBC_NAME)) {
			// Frequency.
			long frequency = pwmChannel.getFrequency();
			frequencyText.setText(String.valueOf(frequency));
			// Enablement.
			boolean enabled = pwmChannel.isEnabled();
			enableButton.setChecked(enabled);
			// Polarity.
			PWMPolarity polarity = pwmChannel.getPolarity();
			switch (polarity) {
				case INVERSED:
					polarityInvertedButton.setChecked(true);
					polarityNormalButton.setChecked(false);
					break;
				case NORMAL:
					polarityNormalButton.setChecked(true);
					polarityInvertedButton.setChecked(false);
					break;
			}
		}
		// Enable the UI.
		enableUI(true);
	}

	/**
	 * Handles what happens when the channel selection changes.
	 */
	private void handleChannelSelectionChanged() {
		selectedChannel = (Integer)channelSelector.getSelectedItem();
		if (selectedChannel == null)
			return;
		try {
			initializePWMChannel();
		} catch (PWMException e) {
			showToast("Error initializing PWM channel: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Handles what happens when the enable button is pressed.
	 */
	private void handleEnableButtonPressed() {
		boolean worked = enablePWM(enableButton.isChecked());
		enableButton.setChecked(enableButton.isChecked() == worked);
		if (enableButton.isChecked()) {
			if (!enablePWM(true))
				enableButton.setChecked(false);
		} else {
			if (!enablePWM(false))
				enableButton.setChecked(true);
		}
	}

	/**
	 * Enables the PWM channel.
	 *
	 * @param enable {@code true} to enable the PWM channel, {@code false} otherwise.
	 *
	 * @return True if success, false otherwise.
	 */
	private boolean enablePWM(boolean enable) {
		if (pwmChannel == null)
			return false;

		try {
			pwmChannel.setEnabled(enable);
			return true;
		} catch (PWMException e) {
			showToast("Error enabling PWM channel: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Handles what happens when the polarity configuration changes.
	 *
	 * @param polarity The new polarity.
	 */
	private void handlePolarityChanged(PWMPolarity polarity) {
		switch (polarity) {
			case NORMAL:
				if (setPolarity(PWMPolarity.NORMAL)) {
					polarityNormalButton.setChecked(true);
					polarityInvertedButton.setChecked(false);
				} else {
					polarityInvertedButton.setChecked(true);
					polarityNormalButton.setChecked(false);
				}
				break;
			case INVERSED:
				if (setPolarity(PWMPolarity.INVERSED)) {
					polarityNormalButton.setChecked(false);
					polarityInvertedButton.setChecked(true);
				} else {
					polarityInvertedButton.setChecked(false);
					polarityNormalButton.setChecked(true);
				}
				break;
		}
	}

	/**
	 * Changes the PWM channel polarity with the given one.
	 *
	 * @param polarity New polarity to set.
	 *
	 * @return {@code true} if polarity was set correctly, {@code false} otherwise.
	 */
	private boolean setPolarity(PWMPolarity polarity) {
		if (pwmChannel == null)
			return false;

		try {
			pwmChannel.setPolarity(polarity);
			return true;
		} catch (PWMException e) {
			showToast("Error changing PWM channel polarity: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Handles what happens when the apply frequency button is pressed.
	 */
	private void handleApplyFrequencyButtonPressed() {
		long frequency;

		String freqString = frequencyText.getText().toString();
		if (freqString.isEmpty()) {
			showToast("Frequency value cannot be empty");
			return;
		}
		try {
			frequency = Long.parseLong(freqString);
			setFrequency(frequency);
		} catch (NumberFormatException e) {
			showToast("The specified frequency is not valid: " + freqString);
		}
	}

	/**
	 * Sets the PWM channel frequency.
	 *
	 * @param frequency The frequency to set.
	 */
	private void setFrequency(long frequency) {
		if (pwmChannel == null)
			return;

		// There might be a problem setting the frequency as configured duty cycle value
		// could be greater than the new period. To avoid it, set duty cycle to a very low value
		// and then restore it.
		int dutyCyclePercentage = -1;
		try {
			dutyCyclePercentage = pwmChannel.getDutyCyclePercentage();
			pwmChannel.setDutyCyclePercentage(0);
			pwmChannel.setFrequency(frequency);
		} catch (IllegalArgumentException | PWMException e) {
			showToast("Error setting PWM channel frequency: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Restore dutyCycle.
			if (dutyCyclePercentage != -1) {
				try {
					pwmChannel.setDutyCyclePercentage(dutyCyclePercentage);
				} catch (Exception ignored) { }
			}
		}
	}

	/**
	 * Handles what happens when the apply duty cycle button is pressed.
	 */
	private void handleApplyDutyCycleButtonPressed() {
		int dutyCycle;

		String dutyCycleString = dutyCycleText.getText().toString();
		if (dutyCycleString.isEmpty()) {
			showToast("Duty cycle value cannot be empty");
			return;
		}
		try {
			dutyCycle = Integer.parseInt(dutyCycleString);
			setDutyCycle(dutyCycle);
		} catch (NumberFormatException e) {
			showToast("The specified duty cycle is not valid: " + dutyCycleString);
		}
	}
	
	/**
	 * Sets the PWM channel duty cycle.
	 * 
	 * @param dutyCycle The Duty cycle to set.
	 */
	private void setDutyCycle(int dutyCycle) {
		if (pwmChannel == null)
			return;

		try {
			pwmChannel.setDutyCyclePercentage(dutyCycle);
		} catch (IllegalArgumentException | PWMException e) {
			showToast("Error setting PWM channel duty cycle percentage: " + e.getMessage());
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
	 * Changes the enable status of the UI.
	 *
	 * @param enable True to enable UI, false to disable.
	 */
	private void enableUI(boolean enable) {
		// CCIMX6 does not allow to modify several of the PWM parameters.
		boolean supported = true;
		if (Build.DEVICE.equals(CCIMX6SBC_NAME)) {
			supported = false;
			(findViewById(R.id.chip_tview)).setVisibility(View.INVISIBLE);
			chipSelector.setVisibility(View.INVISIBLE);
			(findViewById(R.id.polarity_tview)).setVisibility(View.INVISIBLE);
			polarityInvertedButton.setVisibility(View.INVISIBLE);
			polarityNormalButton.setVisibility(View.INVISIBLE);
			enableButton.setVisibility(View.INVISIBLE);
		}
		enableButton.setEnabled(enable && supported);
		frequencyText.setEnabled(enable && supported);
		dutyCycleText.setEnabled(enable);
		polarityInvertedButton.setEnabled(enable && supported);
		polarityNormalButton.setEnabled(enable && supported);
		applyFrequencyButton.setEnabled(enable && supported);
		applyDutyCycleButton.setEnabled(enable);
	}
}
