package shree_nagari.mbank;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.client.android.IntentIntegrator;
import com.google.zxing.client.android.IntentResult;

import java.security.Key;
import java.security.PrivateKey;

import mbLib.MyThread;

public class MainActivity extends Activity implements OnTouchListener {
    public static final int SELECT_PHOTO = 100;
    public int scanOption = 1;
    public int selectedItem = 0;
    private CharSequence mTitle;
    Fragment fragment = null;
    public int frgIndex = -1;
    public String QRCUSTID;
    public String QRDBTACCNO;
    public String QRAMT;
    public String QRCRACCNO;
    public String QRREMARK;
    private FrameLayout frameLayout;
    private MyThread t1;
    //int timeOutInSecs=300;
    static PrivateKey var1 = null;
    static String var5 = "", var3 = "";
    Key var2 = null;
    //public String onback="";

    public void onBackPressed() {
        Log.e("TAG", "FragIndex:- " + frgIndex);
        if (frgIndex < 10) {
            Intent in = new Intent(this, NewDashboard.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            finish();
        } else if (frgIndex == 11) {
            Bundle bundle = new Bundle();
            Fragment fragment = new HomeFragment(this);
            bundle.putInt("CHECKACTTYPE", 1);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 1;
        } else if (frgIndex == 21) {
            Bundle bundle = new Bundle();
            Fragment fragment = new HomeFragment(this);
            bundle.putInt("CHECKACTTYPE", 2);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 2;
        } else if (frgIndex == 31) {
            Bundle bundle = new Bundle();
            Fragment fragment = new HomeFragment(this);
            bundle.putInt("CHECKACTTYPE", 3);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 3;
        } else if (frgIndex == 41) {
            Fragment miniStmtFragment = new MiniStmtActivity(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, miniStmtFragment).commit();
            frgIndex = 4;

        } else if (frgIndex == 51 || frgIndex == 52 || frgIndex == 53 || frgIndex == 54 || frgIndex == 55 || frgIndex == 56) {
            fragment = new FundTransferMenuActivity(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 5;
        } else if (frgIndex == 511) {
            fragment = new SameBankTransfer(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 5;
        } else if (frgIndex == 521) {
            fragment = new QrcodeSendActivity(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 5;
        } else if (frgIndex == 561) {
            fragment = new ShowAccForQrcode(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 5;
        } else if (frgIndex == 571) {
            fragment = new OtherBankTranUID(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 5;
        } else if (frgIndex == 61 || frgIndex == 62 || frgIndex == 63 || frgIndex == 64 || frgIndex == 65 || frgIndex == 66) {
            fragment = new ManageBeneficiaryMenuActivity(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 6;
        } else if (frgIndex == 651) {
            fragment = new AddOtherBankBeneficiary(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 65;
        } else if (frgIndex == 661) {
            fragment = new EditOtherBankBeneficiary(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 66;
        } else if (frgIndex == 71 || frgIndex == 72 || frgIndex == 73) {
            fragment = new ChequeMenuActivity(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 7;
        } else if (frgIndex == 81 || frgIndex == 82 || frgIndex == 83 || frgIndex == 813 || frgIndex == 814 || frgIndex == 86 || frgIndex == 87) {
            fragment = new OtherServicesMenuActivity(this);
            Bundle bundle = new Bundle();
            if (frgIndex == 86 || frgIndex == 87) {
                bundle.putString("PPS_Menus", "PPS");
            } else if (frgIndex == 813 || frgIndex == 814) {
                bundle.putString("PPS_Menus", "EMAIL");
            }
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 8;
        } else if (frgIndex == 74) {
            fragment = new ChequeStatus(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 73;
        } else if (frgIndex == 541) {
            fragment = new TransferHistory(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 56;
        } else if (frgIndex == 9999 || frgIndex == 9990) {
            Intent in = new Intent(this, NewDashboard.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            finish();
        } else if (frgIndex == 9991) {
            Intent in = new Intent(this, NewDashboard.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            finish();
        } else if (frgIndex == 10) {
            Intent in = new Intent(this, NewDashboard.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            //((Activity) this).overridePendingTransition(0, 0);
            finish();

        } else if (frgIndex == 1002 || frgIndex == 1001) {
            fragment = new Notifications(this);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            frgIndex = 4;
        } else if (frgIndex == 1111 || frgIndex == 1112) {
            Intent in = new Intent(this, NewDashboard.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            finish();
        }

    }

    MainActivity mainactivity;

    // @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        var1 = (PrivateKey) getIntent().getSerializableExtra("VAR1");
        var3 = (String) getIntent().getSerializableExtra("VAR3");
        Bundle b1 = this.getIntent().getExtras();

        if (b1 != null) {
            frgIndex = b1.getInt("FRAGINDEX");
        }

        //onback = this.getIntent().getStringExtra("PPS_Menus");

        frameLayout = (FrameLayout) findViewById(R.id.frame_container);
        frameLayout.setOnTouchListener(this);
        Log.e("MAIN ACTIVITY==", "frgIndex====" + frgIndex);//+" onback:- "+onback);
        t1 = new MyThread(Integer.parseInt(getString(R.string.timeOutInSecs)), this, var1, var3);
        t1.start();
        displayView(frgIndex);
    }

    // @SuppressLint("NewApi")
    public void displayView(int position) {
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                fragment = new HomeFragment(this);
                bundle.putInt("CHECKACTTYPE", 1);
                break;
            case 1:
                fragment = new HomeFragment(this);
                bundle.putInt("CHECKACTTYPE", 2);
                break;
            case 2:
                fragment = new HomeFragment(this);
                bundle.putInt("CHECKACTTYPE", 3);
                break;
            case 3:
                fragment = new MiniStmtActivity(this);
                break;
            case 4:
                fragment = new FundTransferMenuActivity(this);
                break;
//		case 5:
//			fragment = new ManageBeneficiaryMenuActivity(this);
//			break;
            case 6:
                fragment = new ChequeMenuActivity(this);
                break;
            case 7:
                fragment = new OtherServicesMenuActivity(this);
                bundle.putString("PPS_Menus", "EMAIL");
                break;
		/*case 8:
			fragment = new EnableDisableATMCard(this);
			break;*/
            case 9999:
                fragment = new Notifications(this);
                break;
            case 9990:
                fragment = new Help(this);
                break;
            case 9991:
                fragment = new ChangeMpinActivity(this);
                break;

            case 10:
                fragment = new ManageBeneficiaryMenuActivity(this);
                //bundle.putString("PPS_Menus", "PPS");
                break;
            default:
                break;

        }
        fragment.setArguments(bundle);
        if (fragment != null && position < 3) {
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        } else if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            //Log.e("MainActivity", "Error in creating fragment");
        }
    }

    // @SuppressLint("NewApi")
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        // getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        /* mDrawerToggle.syncState(); */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                QRCRACCNO = result.getContents();

                if (validateAccNo(QRCRACCNO)) {
                    QRCRACCNO = QRCRACCNO.substring(0, QRCRACCNO.length() - 1);

                    if (QRCRACCNO.equals(QRDBTACCNO)) {
                        Toast.makeText(this, "Can Not Transfer To Same Account", Toast.LENGTH_LONG).show();
                    } else {
                        Fragment fragment = new QrcodeSendActivity(this, QRCUSTID, QRDBTACCNO, QRAMT, QRCRACCNO, QRREMARK);
                        this.setTitle(getString(R.string.lbl_qr_send));
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                        this.frgIndex = 55;
                    }
                } else {
                    Toast.makeText(this, "Invalid Account Number", Toast.LENGTH_LONG).show();
                    Fragment fragment = new FundTransferMenuActivity(this);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                    this.frgIndex = 5;
                }
				/*QRCRACCNO=result.getContents();
				fragment = new QrcodeSendActivity(this,QRCUSTID,QRDBTACCNO, QRAMT,QRCRACCNO,QRREMARK);
				this.setTitle(getString(R.string.lbl_qr_send));
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				*/
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean validateAccNo(String str) {
        String regex = "[0-9]+";
        if (str.matches(regex)) {
            int sum = 0, grandSum = 0;
            String str2 = str.substring(0, str.length() - 1);

            for (int i = 0; i < str2.length(); i++) {
                sum = sum + Integer.parseInt("" + str2.charAt(i));
            }
            while (sum > 9) {
                grandSum = 0;
                while (sum > 0) {
                    int rem;
                    rem = sum % 10;
                    grandSum = grandSum + rem;
                    sum = sum / 10;
                }
                sum = grandSum;
            }
            if (grandSum == Integer.parseInt(str.substring(str.length() - 1, str.length())))
                return true;
            else
                return false;
        } else
            return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        t1.sec = Integer.parseInt(getString(R.string.timeOutInSecs));
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        t1.sec = Integer.parseInt(getString(R.string.timeOutInSecs));
        Log.e("sec11= ", "sec11==" + t1.sec);
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        t1.sec = -1;

    }


}
