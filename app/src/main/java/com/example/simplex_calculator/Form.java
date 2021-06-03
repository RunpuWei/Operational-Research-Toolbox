package com.example.simplex_calculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.CellRange;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.draw.TextDrawFormat;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.data.table.ArrayTableData;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Form extends AppCompatActivity {
    SmartTable table;
    private Button CalBut;

    int columnNumber;//列数
    int lineNumber;//行数
    //private String MaxOrMin;
    private SimplexMethod arthmetic;//单纯形法算法
    private CommonFraction[][] Coefficient_A;//技术系数A
    private CommonFraction[] Coefficient_B;//限额系数B
    private CommonFraction[] Coefficient_C;//价值系数C
    private boolean isUnconstrainedVariable[];//每个变量是否为非约束变量
    private String isEquality[];//每一列的符号（大于小于等于）情况
    private boolean isEnd = false;//是否结束
    private CommonFraction[] optimalSolution;//最优值
    private boolean isMax = true;//是否求最大值
    private CommonFraction[] checkingNumbers;//检验数
    private CommonFraction[] coefficient_theta;//θ
    private int positionOfBasicVariable[];//基变量对应角标
    int time=0;//防止无限迭代
    private String[][] infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Intent intent=getIntent();
        String code=intent.getStringExtra("code");//获取首页传过来的未被格式化过的代码
        CodeFormat code_hasFormat =new CodeFormat(code);//被格式化的code
        //table.addData();
        this.lineNumber=code_hasFormat.getLineNumber();
        this.columnNumber=code_hasFormat.getColumnNumber();
        this.Coefficient_A= code_hasFormat.getCoefficient_A();
        this.Coefficient_B= code_hasFormat.getCoefficient_B();
        this.Coefficient_C= code_hasFormat.getCoefficient_C();
        this.isUnconstrainedVariable=code_hasFormat.isUnconstrainedVariable();
        this.isEquality=code_hasFormat.getisEquality();
        this.isMax= code_hasFormat.IsMax();
        SimplexMethod arithmetic = new SimplexMethod(Coefficient_A, Coefficient_B, Coefficient_C, isUnconstrainedVariable, isEquality, isMax);
        this.arthmetic = arithmetic;
        arithmetic.initialize();//单纯形表的初始化
        table = (SmartTable) findViewById(R.id.table);
        CalBut=findViewById(R.id.CalBut);//迭代一次按钮


        /*String[] title = {"单纯形法计算结果"};
        ArrayTableData<String> tableData = ArrayTableData.create("单纯形法计算结果",title,getTable(),new TextDrawFormat<String>());
        //点击事件
        tableData.setOnItemClickListener(new ArrayTableData.OnItemClickListener<Integer>() {
            @Override
            public void onClick(Column column, String value, Integer o, int col, int row) {
                Toast.makeText(Form.this,"列:"+col+ " 行："+row + "数据："+value,Toast.LENGTH_SHORT).show();
            }
        });
        //设置表格数据
        table.setTableData(tableData);*/


        CalBut.setOnClickListener(new View.OnClickListener() {//监听按钮点击动作

            @Override
            public void onClick(View v) {
                if(isEnd){
                    Toast.makeText(Form.this, "迭代已经结束啦~~不要再点了啦~~", Toast.LENGTH_LONG).show();
                    return;
                }
                if(time >50) {
                    Toast.makeText(Form.this, "迭代次数太多,强行终止，你手不嫌疼，我还嫌麻烦呢哼~", Toast.LENGTH_LONG).show();
                    System.out.println("迭代次数太多,强行终止，你手不嫌疼，我还嫌麻烦呢哼~");
                    isEnd = true;
                }
                else{
                    time++;
                    arithmetic.judge();
                    if(arithmetic.result != "" && !arthmetic.result.equals("无界解")){
                        for(int i=0; i<lineNumber; i++) {
                            arithmetic.coefficient_theta[i] = null;
                        }
                    }
                    transmitToTable();
                    for(int i=0; i<arithmetic.matrixAB.matrix.length; i++) {//输出矩阵
                        for(int j=0; j<arithmetic.matrixAB.matrix[0].length; j++) {
                            System.out.printf("%10s", arithmetic.matrixAB.matrix[i][j]);
                        }
                        System.out.print("\n");
                    }

                    if(arithmetic.result != "") {
                        isEnd = true;
                        System.out.println(arithmetic.result);
                        if(arithmetic.result.equals("有唯一最优解")) {
                            optimalSolution = new CommonFraction[arithmetic.optimalSolution.length];
                            optimalSolution = arithmetic.optimalSolution;
                            String solution = "最";
                            if(arithmetic.isMax) {
                                solution += "大值为:";
                            }
                            else {
                                solution += "小值为:";
                            }
                            solution += arithmetic.optimalValue.getCommonFraction();

                            System.out.println(solution);
//改完
                            String result = "(";
                            for(int i=0; i<arithmetic.optimalSolution.length; i++) {
                                result += arithmetic.optimalSolution[i].getCommonFraction();
                                if(i != arithmetic.optimalSolution.length-1) {
                                    result += ", ";
                                }
                            }
                            result += ")";
                            System.out.println("最优解为:"+result);
                            for(CommonFraction cf :arithmetic.optimalSolution) {

                                System.out.println(cf + " ");
                            }
                            System.out.println();
                            System.out.println("最优值是:"+arithmetic.optimalValue);

                            printTable(arithmetic,solution,result);
                            Toast.makeText(Form.this, "迭代结束，共迭代"+time+"次\n"+solution+"\n"+"最优解为:"+result, Toast.LENGTH_LONG).show();
                        }
                        else if(arithmetic.result.equals("无界解")){
                            printTable(arithmetic,null,null);
                            Toast.makeText(Form.this, "迭代结束，共迭代"+time+"次\n"+"无界解", Toast.LENGTH_LONG).show();
                        }else if(arithmetic.result.equals("无可行解")){
                            printTable(arithmetic,null,null);
                            Toast.makeText(Form.this, "迭代结束，共迭代"+time+"次\n"+"无可行解", Toast.LENGTH_LONG).show();
                        }else if(arithmetic.result.equals("无穷多最优解")){
                            printTable(arithmetic,null,null);
                            Toast.makeText(Form.this, "迭代结束，共迭代"+time+"次\n"+"无穷多最优解", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(Form.this, "迭代中，现在是第"+time+"次迭代", Toast.LENGTH_LONG).show();
                        System.out.println("迭代中，现在是第"+time+"次迭代");
                        printTable(arithmetic,null,null);
                        arithmetic.iteration();
                    }

                }

            }
        });
        };

    //矩阵转置方法
    private static String[][] transpose(String[][] A) {
        int R = A.length, C = A[0].length;
        String[][] ans = new String[C][R];
        for (int r = 0; r < R; r++)
            for (int c = 0; c < C; c++)
                ans[c][r] = A[r][c];
        return ans;
    }

    //打印表格方法
    private void printTable(SimplexMethod arithmetic,String solution,String result) {

        int tableColumn=4+arithmetic.COLUMNNUMBER;
        String[] title =new String[tableColumn];
        Arrays.fill(title," ");

        ArrayTableData<String> tableData = ArrayTableData.create("第"+(time)+"次迭代",title,getTable(arithmetic,solution,result),new TextDrawFormat<String>());

        //tableData.setUserCellRange(cellRange);

        //设置合并单元格
        List<CellRange> cellRange=new ArrayList<CellRange>();
        cellRange.add(new CellRange(0,0,0,2));//合并Cj
        cellRange.add(new CellRange(0,1,3+arithmetic.COLUMNNUMBER,3+arithmetic.COLUMNNUMBER));//合并θ
        cellRange.add(new CellRange(2+arthmetic.LINENUMBER,2+arthmetic.LINENUMBER,0,2));//合并检验数
        cellRange.add(new CellRange(3+arithmetic.LINENUMBER,3+arithmetic.LINENUMBER,0,3+arithmetic.COLUMNNUMBER));//合并输出
        tableData.setUserCellRange(cellRange);


        //table.getConfig().setContentBackground(new BaseBackgroundFormat(0xffeaeaea));//设置表格背景，但是我不会改颜色呜呜，所以搁置了

        //设置表格数据
        table.setTableData(tableData);
    }


    private void transmitToTable(){
        this.lineNumber = arthmetic.matrixAB.matrix.length;
        this.columnNumber = arthmetic.matrixAB.matrix.length - 1;

        this.Coefficient_A = new CommonFraction[this.lineNumber][this.columnNumber];
        this.Coefficient_A = arthmetic.getA().matrix;
        this.Coefficient_B = new CommonFraction[this.lineNumber];
        for(int i=0; i<lineNumber; i++) {
            this.Coefficient_B[i] = arthmetic.getB().matrix[i][0];
        }
        this.Coefficient_C = new CommonFraction[this.columnNumber];
        this.Coefficient_C = arthmetic.coefficient_C;
        this.checkingNumbers = new CommonFraction[this.columnNumber];
        this.checkingNumbers = arthmetic.checkingNumbers;
        this.coefficient_theta = new CommonFraction[this.lineNumber];
        this.coefficient_theta = arthmetic.coefficient_theta;
        this.positionOfBasicVariable = new int[this.lineNumber];
        this.positionOfBasicVariable = arthmetic.positionsOfBasicVariable;

    }

    //获取现在的二维数组应该是啥东西~
    private String[][] getTable(SimplexMethod arithmetic,String solution,String result){
        String [][]temp= new String[arthmetic.LINENUMBER+4][arthmetic.COLUMNNUMBER+4];//当前式子的行数+4 当前式子的列数+4

        //写入第一行数据
        temp[0][0]="Cj";
        temp[0][1]="Cj";
        temp[0][2]="Cj";
        for(int i=0;i<arthmetic.getVarNum();i++){//获取价值系数
            temp[0][3+i]=arthmetic.coefficient_C[i].toString();
        }

        temp[0][arthmetic.COLUMNNUMBER+3]="θ";//写入θ


        //写入第二行数据
        temp[1][0]="CB";
        temp[1][1]="XB";
        temp[1][2]="b";
        for(int i=0;i<arthmetic.getVarNum();i++){
            temp[1][3+i]="X"+(i+1);
        }
        temp[1][arthmetic.COLUMNNUMBER+3]="θ";//写入θ

        //开始写入矩阵
        int []BasicVariable=arthmetic.positionsOfBasicVariable;//获取基变量的角标矩阵；
        for(int i=0;i<arthmetic.LINENUMBER;i++){
            int nowBasicVariable=BasicVariable[i];//现在的基变量的角标
            String nowBasicVariableCb=arthmetic.coefficient_C[nowBasicVariable].toString();//现在这一行的基变量的CB
            temp[i+2][0]=nowBasicVariableCb;
            temp[i+2][1]="X"+(nowBasicVariable+1);
            temp[i+2][2]=arthmetic.matrixAB.matrix[i][arthmetic.COLUMNNUMBER].toString();

            for(int j=0;j<arthmetic.COLUMNNUMBER;j++){
                temp[i+2][3+j]=arthmetic.matrixAB.matrix[i][j].toString();
            }

            if(arthmetic.coefficient_theta[i]!=null){
                temp[i+2][arthmetic.COLUMNNUMBER+3]=arthmetic.coefficient_theta[i].toString();
            }
            else{
                temp[i+2][arthmetic.COLUMNNUMBER+3]="--";
            }



        }
        temp[2+arthmetic.LINENUMBER][0]="σj=cj-zj";
        for(int i=0;i<arthmetic.COLUMNNUMBER;i++){
            temp[2+arthmetic.LINENUMBER][3+i]=arthmetic.checkingNumbers[i].toString();
        }
        temp[2+arthmetic.LINENUMBER][3+arthmetic.COLUMNNUMBER]="By Bailing";
        if(isEnd&&arithmetic.result.equals("有唯一最优解")){
            temp[arthmetic.LINENUMBER+3][0]="最优解为："+result+"  最优值是："+arithmetic.optimalValue.toString();
        }
        if(isEnd&&arithmetic.result.equals("无界解")){
            temp[arthmetic.LINENUMBER+3][0]="无界解";
        }
        if(isEnd&&arithmetic.result.equals("无可行解")){
            temp[arthmetic.LINENUMBER+3][0]="无可行解";
        }
        if(isEnd&&arithmetic.result.equals("无穷多最优解")){
            temp[arthmetic.LINENUMBER+3][0]="无穷多最优解";
        }

        return transpose(temp);//奇奇怪怪的表格框架，为啥行列是反的-.-、、所以这个是做了个转置哈哈哈
    }
}