package com.example.simplex_calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeFormat {// CodeFormat类用来格式化用户输入的代码
    private String Initial_code;// 初始代码
    // private String Rows_Objective_function;//目标函数的行数
    private String[] Per_code;// 每行代码
    private int ConstraintsNum;// 约束的数量
    private int VariablesNum;// 变量的数量

    public CodeFormat(String Initial_code) {// 初始化 Initial_code 用户输入的初始代码
        this.Initial_code = Initial_code;
        Per_code = this.Initial_code.split("\n");
        ConstraintsNum = getLineNumber();
        VariablesNum = getColumnNumber();
    }

    public String[] getisEquality() {// 获取每个约束的符号情况

        String[] isEquality = new String[ConstraintsNum];
        for (int i = 0; i < ConstraintsNum; i++) {
            if (Per_code[i + 1].contains(">") || Per_code[i + 1].contains("≥")) {
                isEquality[i] = ">=";
            } else if (Per_code[i + 1].contains("<") || Per_code[i + 1].contains("≤")) {
                isEquality[i] = "<=";
            } else if (Per_code[i + 1].contains("=")) {
                isEquality[i] = "=";
            }
        }

        return isEquality;
    }

    private int getNumeric(String str) {// 正则表达式获取字符串中的数字
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        int NumberNum = Integer.parseInt(m.replaceAll("").trim());
        return NumberNum;
    }

    public boolean[] isUnconstrainedVariable() {// 返回是否是无约束变量 true->是 false->不是
        boolean isUnconstrainedVariable[] = new boolean[VariablesNum];
        Arrays.fill(isUnconstrainedVariable, false);

        for (int i = 0; i < Per_code.length; i++) {
            if (Per_code[i].contains("@UV")||Per_code[i].contains("@uv")) {
                int indexOfUV = getNumeric(Per_code[i]) - 1;
                isUnconstrainedVariable[indexOfUV] = true;
            }
        }
        return isUnconstrainedVariable;
    }

    public CommonFraction[] getCoefficient_B() {// 获取限额系数B数组

        CommonFraction[] Coefficient_B = new CommonFraction[ConstraintsNum];
        for (int i = 0; i < ConstraintsNum; i++) {
            String[] line_temp = Per_code[i + 1].split(" ");// 第一行是目标函数，所以需要跳过第一行~
            Coefficient_B[i] = new CommonFraction(line_temp[line_temp.length - 1]);
        }

        return Coefficient_B;
    }

    public CommonFraction[][] getCoefficient_A() {// 获取技术系数A矩阵
        CommonFraction[][] Coefficient_A = new CommonFraction[ConstraintsNum][VariablesNum];
        for (int i = 0; i < ConstraintsNum; i++) {
            for (int j = 0; j < VariablesNum; j++) {
                String[] line_temp = Per_code[i + 1].split(" ");// 第一行是目标函数，所以需要跳过第一行~
                Coefficient_A[i][j] = new CommonFraction(line_temp[j]);
            }
        }
        return Coefficient_A;
    }

    public boolean IsMax() {// 判断代码要求最大值还是最小值
        if (Per_code[0].substring(0, 2).contains("a")||Per_code[0].substring(0, 2).contains("A")) {//max MAX Max奇奇怪怪的输入~
            return true;
        } else if (Per_code[0].substring(0, 2).contains("i")||Per_code[0].substring(0, 2).contains("I")) {
            return false;
        }

        return true;
    }

    public int getLineNumber() {// 获取行的数量
        return getConstraintsNum();
    }

    public int getConstraintsNum() {// 获取约束的数量
        int temp = 0;// 临时储存探测到的约束的数量；
        for (int i = 0; i < Per_code.length; i++) {
            if (Per_code[i].contains("=") || Per_code[i].contains("=") || Per_code[i].contains("<=")
                    || Per_code[i].contains(">=") || Per_code[i].contains("≥") || Per_code[i].contains("≤")) {
                temp++;
            }
        }
        return temp;
    }

    public int getColumnNumber() {// 获取列的数量
        return getVariablesNum();
    }

    public int getVariablesNum() {// 获取变量的数量
        String[] Coefficient_C = Per_code[0].substring(4).split(" ");
        return Coefficient_C.length;
    }

    public CommonFraction[] getCoefficient_C() {// 获取目标函数价值系数数组
        String[] Coefficient_C = Per_code[0].substring(4).split(" ");

        CommonFraction[] VariablesFraction = new CommonFraction[VariablesNum];
        for (int i = 0; i < VariablesNum; i++) {
            VariablesFraction[i] = new CommonFraction(Coefficient_C[i]);
        }
        return VariablesFraction;
    }

    /*public static void main(String args[]) {// 测试专用
        String testcode = "max 2 1 5 6\n2 0 1 1 <= 8\n2 2 1 2 <= 12";
        CodeFormat test = new CodeFormat(testcode);
        System.out.println(test.Per_code);
        System.out.println(test.getColumnNumber());
        System.out.println(test.getConstraintsNum());
        System.out.println(test.getLineNumber());
        System.out.println(test.getVariablesNum());
        System.out.println("stop");
        CommonFraction[] C = test.getCoefficient_C();
        CommonFraction[] B = test.getCoefficient_B();
        CommonFraction[][] A = test.getCoefficient_A();
        boolean isUnconstrainedVariable[] = test.isUnconstrainedVariable();
        String isEquality[] = test.getisEquality();
        boolean isMax = test.IsMax();
        //SimplexMethod arthmetic=new SimplexMethod();
        Console console = new Console(test.getColumnNumber(), test.getLineNumber(), test.IsMax(),
                test.getCoefficient_A(), test.getCoefficient_B(), test.getCoefficient_C(),
                test.isUnconstrainedVariable(), test.getisEquality());
        console.start();
        System.out.println("stop");
    }*/

}
