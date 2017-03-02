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

public class PolicySetupActivity extends Activity {
    private static final int REQ_ACTIVATE_DEVICE_ADMIN = 10;

    DevicePolicyManager devicePolicyManager;
    private ComponentName mPolicyAdmin;
    private Context context;

    private int mCurrentScreenId;
    private int amountFailedPasswordsForWipe = 10; /* default */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        mPolicyAdmin = new ComponentName(context, PolicyAdmin.class);
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
            devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.setMaximumFailedPasswordsForWipe(mPolicyAdmin, amountFailedPasswordsForWipe);

            Toast.makeText(this, getString(R.string.successfully_activated, amountFailedPasswordsForWipe), Toast.LENGTH_SHORT).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initPolicySetupScreen() {
        EditText amountFailedPasswordsForWipeInputField = (EditText) findViewById(R.id.policy_password_length);
        amountFailedPasswordsForWipeInputField.setText(String.valueOf(amountFailedPasswordsForWipe));
    }

    private View.OnClickListener mActivateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent activateDeviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mPolicyAdmin);

            activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.device_admin_activation_message));
            startActivityForResult(activateDeviceAdminIntent, REQ_ACTIVATE_DEVICE_ADMIN);
        }
    };

    private void setupNavigation(View.OnClickListener listener) {
        Button actionBtn = (Button) findViewById(R.id.setup_action_btn);
        actionBtn.setOnClickListener(listener);
    }

    private void setScreenContent(final int screenId) {
        mCurrentScreenId = screenId;
        setContentView(mCurrentScreenId);

        if(!devicePolicyManager.isAdminActive(mPolicyAdmin)) {
            initPolicySetupScreen();
            setupNavigation(mActivateButtonListener);
        }
        else {
            //Toast.makeText(this, "Already ", Toast.LENGTH_SHORT).show();
        }
    }

    public static class PolicyAdmin extends DeviceAdminReceiver {
        @Override
        public void onDisabled(Context context, Intent intent) {
            super.onDisabled(context, intent);
        }
    }
}

