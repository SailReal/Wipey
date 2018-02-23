package com.raufelder.wipey;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.LENGTH_SHORT;
import static com.raufelder.wipey.Preferences.amountOfFailedLogin;
import static com.raufelder.wipey.Preferences.isWipeActivated;
import static com.raufelder.wipey.Preferences.saveRestart;
import static com.raufelder.wipey.Preferences.saveWipe;
import static com.raufelder.wipey.Preferences.setAmountOfFailedLogin;

public class PolicySetupActivity extends Activity {
	private static final int REQ_ACTIVATE_DEVICE_ADMIN = 10;

	private DevicePolicyManager devicePolicyManager;
	private ComponentName policyAdmin;
	private Context context;

	@BindView(R2.id.action_button)
	Button actionButton;
	@BindView(R2.id.amount_failed_passwords_for_wipe)
	EditText amountFailedPasswordsForWipeInputField;
	@BindView(R2.id.reboot)
	RadioButton rebootRadio;
	@BindView(R2.id.wipe_data)
	RadioButton wipeData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		policyAdmin = new ComponentName(context, PolicyAdmin.class);
		devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setScreenContent(R.layout.activity_policy_setup);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_ACTIVATE_DEVICE_ADMIN && resultCode == RESULT_OK) {
			devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

			int amount = amountOfFailedLogin(context);
			if (isWipeActivated(context)) {
				devicePolicyManager.setMaximumFailedPasswordsForWipe(policyAdmin, amount);
				Toast.makeText(this, getString(R.string.successfully_activated_wipe, amount), LENGTH_SHORT).show();
			} else {
				devicePolicyManager.setMaximumFailedPasswordsForWipe(policyAdmin, Integer.MAX_VALUE);
				Toast.makeText(this, getString(R.string.successfully_activated_reboot, amount), LENGTH_SHORT).show();
			}

		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@OnClick(R2.id.action_button)
	public void submitButton(View view) {
		try {
			int amountFailedPasswordsForWipe = Integer.parseInt(amountFailedPasswordsForWipeInputField.getText().toString());
			if (amountFailedPasswordsForWipe > 0) {
				setAmountOfFailedLogin(context, amountFailedPasswordsForWipe);
			}
		} catch (NumberFormatException e) {
			Toast.makeText(context, "Please insert only numbers", LENGTH_SHORT).show();
		}

		if (wipeData.isChecked()) {
			saveWipe(context);
		} else {
			saveRestart(context);
		}

		Intent activateDeviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyAdmin);

		activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.device_admin_activation_message));
		startActivityForResult(activateDeviceAdminIntent, REQ_ACTIVATE_DEVICE_ADMIN);
	}

	private void setScreenContent(final int screenId) {
		setContentView(screenId);
		ButterKnife.bind(this);

		amountFailedPasswordsForWipeInputField.setText(String.valueOf(amountOfFailedLogin(context)));
		if (isWipeActivated(context)) {
			wipeData.setChecked(true);
			rebootRadio.setChecked(false);
		} else {
			wipeData.setChecked(false);
			rebootRadio.setChecked(true);
		}
	}

	public static class PolicyAdmin extends DeviceAdminReceiver {
		@Override
		public void onPasswordFailed(Context context, Intent intent) {
			handlePasswordFailed(context);
		}

		@Override
		public void onPasswordFailed(Context context, Intent intent, UserHandle user) {
			handlePasswordFailed(context);
		}

		private void handlePasswordFailed(Context context) {
			if (!isWipeActivated(context)) {
				DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				if (devicePolicyManager.getCurrentFailedPasswordAttempts() >= amountOfFailedLogin(context)) {
					try {
						Process proc = Runtime.getRuntime()
											  .exec(new String[]{"su", "-c", "reboot -p"});
						proc.waitFor();
					} catch (Exception ex) {
						Log.e("PolicyAdmin", "Failed to reboot");
					}
				}
			}
		}
	}

}

