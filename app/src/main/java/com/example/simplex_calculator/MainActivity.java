package com.example.simplex_calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button CalBut;//计算按钮
    private Button ButLT;//小于号
    private Button ButGT;//大于号
    private Button ButET;//等于号
    private Button Butmin;//最小
    private Button Butmax;//最大
    private EditText ETextMatrix;//矩阵输入框


    /*private int lineNumber;//行数
    private int columnNumber;//列数
    private boolean isMax=true;//求最大值or求最小值
    private CommonFraction[][] Coefficient_A;//技术系数A
    private CommonFraction[] Coefficient_B;//限额系数B
    private CommonFraction[] Coefficient_C;//价值系数C
    private boolean isUnconstrainedVariable[]; //每个变量是否是非约束型变量
    private String isEquality[];//每个约束的符号情况（>= <= =）*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.about:
                //setPortParameters(1200,mParity);
                new AlertDialog.Builder(this)
                        .setTitle("关于本工具")
                        .setMessage("\n本工具作者：卫闰朴\n\n部分代码源自“王嘉俊”\n\n好吧，虽然叫工具箱，但是目前只有单纯形法迭代，后边估计会加其他功能哒~（会吧，会吧......）\n\n如果发现本工具任何BUG，请联系QQ1270474154")
                        .setPositiveButton("是", null)
                        .show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalBut=findViewById(R.id.Calculator);
        ButLT=findViewById(R.id.ButLT);
        ButGT=findViewById(R.id.ButGT);
        ButET=findViewById(R.id.ButET);
        Butmin=findViewById(R.id.Butmin);
        Butmax=findViewById(R.id.Butmax);
        ETextMatrix=findViewById(R.id.ETextMatrix);
        CalBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText EditText_Code=(EditText)findViewById(R.id.ETextMatrix);
                String code=EditText_Code.getText().toString();
                //System.out.println(code);
                try {
                    CodeFormat checkFormat=new CodeFormat(code);
                    //跳转到计算界面
                    Intent intent=new Intent(MainActivity.this,Form.class);
                    intent.putExtra("code",code);
                    startActivity(intent);
                    //MainActivity.this.startActivity(itent);
                    //MainActivity.this.finish();
                }catch (RuntimeException e){
                    Toast.makeText(MainActivity.this, "你输入的格式貌似是有问题，请先检查一下吧~\n我差点就崩溃了呜呜o(╥﹏╥)o", Toast.LENGTH_LONG).show();
                    return;
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "你输入的格式貌似是有问题，请先检查一下吧~\n你都把我搞崩溃了呜呜o(╥﹏╥)o", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        ButLT.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String newText=ETextMatrix.getText()+"<=";
                ETextMatrix.setText(newText);//光标定位到最后一位
                ETextMatrix.setSelection(newText.length());
            }
        });
        ButGT.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String newText=ETextMatrix.getText()+">=";
                ETextMatrix.setText(newText);//光标定位到最后一位
                ETextMatrix.setSelection(newText.length());
            }
        });
        ButET.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String newText=ETextMatrix.getText()+"=";
                ETextMatrix.setText(newText);//光标定位到最后一位
                ETextMatrix.setSelection(newText.length());
            }
        });
        Butmin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String newText=ETextMatrix.getText()+"min";
                ETextMatrix.setText(newText);//光标定位到最后一位
                ETextMatrix.setSelection(newText.length());
            }
        });
        Butmax.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String newText=ETextMatrix.getText()+"max";
                ETextMatrix.setText(newText);//光标定位到最后一位
                ETextMatrix.setSelection(newText.length());
            }
        });
    }
}