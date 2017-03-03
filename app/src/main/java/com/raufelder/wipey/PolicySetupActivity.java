package com.raufelder.wipey;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PolicySetupActivity extends Activity {
    private static final int REQ_ACTIVATE_DEVICE_ADMIN = 10;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName policyAdmin;

    private int currentScreenId;
    private int amountFailedPasswordsForWipe = 10; /* default */

    private @BindView(R2.id.action_button) Button actionButton;
    private @BindView(R2.id.amount_failed_passwords_for_wipe) EditText amountFailedPasswordsForWipeInputField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();

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
    public void onBackPressed() { super.onBackPressed(); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ACTIVATE_DEVICE_ADMIN && resultCode == RESULT_OK) {
            devicePolicyManager = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.setMaximumFailedPasswordsForWipe(policyAdmin, amountFailedPasswordsForWipe);

            Toast.makeText(this, getString(R.string.successfully_activated, amountFailedPasswordsForWipe), Toast.LENGTH_SHORT).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initPolicySetupScreen() {
        amountFailedPasswordsForWipeInputField.setText(String.valueOf(amountFailedPasswordsForWipe));
    }

    @OnClick(R2.id.action_button)
    public void submitButton(View view) {
        Intent activateDeviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyAdmin);

        activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.device_admin_activation_message));
        startActivityForResult(activateDeviceAdminIntent, REQ_ACTIVATE_DEVICE_ADMIN);
    }

    private void setScreenContent(final int screenId) {
        currentScreenId = screenId;
        setContentView(currentScreenId);
        ButterKnife.bind(this);

        if(!devicePolicyManager.isAdminActive(policyAdmin)) {
            initPolicySetupScreen();
        }
        /*else {
            // TODO change layout
        }*/
    }

    public static class PolicyAdmin extends DeviceAdminReceiver {}
}

