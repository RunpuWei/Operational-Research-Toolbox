package com.example.simplex_calculator;

public class SimplexMethod {
    /*
    实现接口
    */
    /*原代码*/
    public CommonFraction[] coefficient_C;//价值系数c
    public CommonFraction[] checkingNumbers;//检验数
    public CommonFraction[] coefficient_theta;//θ
    public CommonFraction optimalValue;//最优值
    public Matrix matrixAB;//a和b所在矩阵
    public Matrix matrixA;//a所在矩阵
    public Matrix matrixB;//b所在列矩阵
    private boolean isUnconstrainedVariable[];//每个变量是否是无约束变量
    private String isEquality[];//每个约束条件是否是等式
    public static final CommonFraction M = new CommonFraction(5201314);//大M法的M
    public static final CommonFraction NegM = new CommonFraction(-5201314);//大M法的-M
    public static final CommonFraction ONE = new CommonFraction(1);//1
    public static final CommonFraction NegONE = new CommonFraction(-1);//-1
    public static final CommonFraction ZERO = new CommonFraction(0); //0
    public int[] positionsOfBasicVariable;//基变量
    public int LINENUMBER;//列长度
    public int COLUMNNUMBER;//行长度
    public String result = "";//解的情况
    public CommonFraction[] optimalSolution;//最优解
    public int positionOfMinimunTheta;//最小θ值的列位置
    public CommonFraction minimumTheta;//最小的θ值
    public int positionOfMaxCheckingNumber = 0;//最大检验数的位置
    public int numberOfInitialVariables = 0;//初始变量数
    public int numberOfAddedUnconstrainedVariables = 0;//增加的非约束变量数
    public int numberOfChangedPositiveSymbols = 0;//由改变符号而增加的正变量数
    public int numberOfChangedNegtiveSymbols = 0;//又改变符号而增加的负变量数
    public int numberOfAddedM = 0;//增加的大M数
    public boolean isMax;//是否求最大值

    //初始化
    public SimplexMethod(CommonFraction[][] a, CommonFraction[] b, CommonFraction[] c, boolean isUnconstrainedVariable[], String isEquality[], boolean isMax) {
        this.matrixA = new Matrix(a);
        LINENUMBER = this.matrixA.matrix.length;
        this.numberOfInitialVariables = this.matrixA.matrix[0].length;
        this.matrixB = new Matrix(b);
        this.coefficient_C = new CommonFraction[c.length];
        this.coefficient_C = c;
        coefficient_theta = new CommonFraction[LINENUMBER];
        this.isUnconstrainedVariable = isUnconstrainedVariable;
        this.positionsOfBasicVariable = new int[LINENUMBER];
        this.isEquality = isEquality;
        this.isMax = isMax;


    }


    //化为标准形式
    public void initialize() {
        //如果b为负，则自动变号
        for(int i=0; i<LINENUMBER; i++) {
            if(matrixB.matrix[i][0].compare(ZERO) < 0) {
                this.matrixA.lineMultiplication(i, -1);
                this.matrixB.lineMultiplication(i, -1);
                if(isEquality[i].equals("<=")) {
                    isEquality[i] = ">=";
                } else if(isEquality[i].equals(">=")) {
                    isEquality[i] = "<=";
                }
            }
        }

        //如果求最小值，则将价值系数取相反数
        if(!this.isMax) {
            for(int i=0; i<coefficient_C.length; i++) {
                coefficient_C[i] = coefficient_C[i].multiplicationOfCommonFraction(NegONE);
            }
        }


        //将无约束变量Xi转化成X1-X2的形式,并将新的列向量添加到矩阵中
        for(int j=0; j<isUnconstrainedVariable.length;j++) {
            if(isUnconstrainedVariable[j]) {
                CommonFraction[] ColumnCommonFraction = new CommonFraction[LINENUMBER];
                for(int i=0; i<LINENUMBER; i++) {
                    ColumnCommonFraction[i] = this.matrixA.matrix[i][j].multiplicationOfCommonFraction(NegONE);
                }
                Matrix ColunmMatrix = new Matrix(ColumnCommonFraction);
                this.matrixA = this.matrixA.addMatrix(ColunmMatrix);
                addC(coefficient_C[j].multiplicationOfCommonFraction(NegONE).getCommonFraction());
                this.numberOfAddedUnconstrainedVariables++;//增加的非约束变量数+1
            }
        }

        //加松弛变量
        for(int i=0; i<LINENUMBER; i++) {
            if(isEquality[i].equals("<=")) {
                insertColumn(i, ONE, ZERO, "0");
                this.numberOfChangedPositiveSymbols++;//由改变符号而增加的变量数+1
            }
            else if(isEquality[i].equals(">=")) {
                insertColumn(i, NegONE, ZERO, "0");
                this.numberOfChangedNegtiveSymbols++;
            }
            else {
                continue;
            }

        }


        //判断哪些行有单位矩阵
        boolean[] isUnitMatrix = new boolean[LINENUMBER];
        for(int i=0; i<LINENUMBER; i++) {
            isUnitMatrix[i] = false;//初始化数组
        }
        for(int j=0; j<this.matrixA.matrix[0].length; j++) {
            int nonZero = 0;
            int positionOfNonZero = -1;
            boolean isNegtive = false;
            for(int i=0; i<LINENUMBER; i++) {
                //不等于0
                if(!this.matrixA.matrix[i][j].getCommonFraction().equals("0")) {
                    nonZero++;
                    positionOfNonZero = i;
                    //如果小于0需要做标记
                    if(this.matrixA.matrix[i][j].compare(ZERO) < 0) {
                        isNegtive = true;
                    }
                }
            }
            if(nonZero == 1 && !isNegtive) {
                if(isUnitMatrix[positionOfNonZero] == false) {
                    String valueOfNonZero = this.matrixA.matrix[positionOfNonZero][j].getCommonFraction();
                    matrixA.lineDivision(positionOfNonZero, new CommonFraction(valueOfNonZero));
                    matrixB.lineDivision(positionOfNonZero, new CommonFraction(valueOfNonZero));
                    isUnitMatrix[positionOfNonZero] = true;
                    this.positionsOfBasicVariable[positionOfNonZero] = j;
                }
            }
        }



        //增加大M
        for(int i=0; i<LINENUMBER; i++) {
            if(!isUnitMatrix[i]) {
                insertColumn(i, ONE, ZERO, NegM.getCommonFraction());
                this.positionsOfBasicVariable[i] = matrixA.matrix[0].length - 1;
                this.numberOfAddedM++;//增加的大M数+1
            }
        }

        //化为标准形式后赋值给AB矩阵
        COLUMNNUMBER = matrixA.matrix[0].length;
        matrixAB = matrixA.addMatrix(matrixB);
    }

    //获取现在的变量数量
    public int getVarNum(){
        return this.numberOfInitialVariables+this.numberOfChangedPositiveSymbols+this.numberOfChangedNegtiveSymbols+this.numberOfAddedUnconstrainedVariables+this.numberOfAddedM;
    }

    //判断结果
    public void judge() {
        this.checkingNumbers = new CommonFraction[COLUMNNUMBER];
        //求检验数
        for(int j=0; j<COLUMNNUMBER; j++) {
            CommonFraction product = ZERO;//CB*aij
            for(int i=0; i<LINENUMBER; i++) {
                product = product.additionOfCommonFraction(this.coefficient_C[positionsOfBasicVariable[i]].multiplicationOfCommonFraction(matrixAB.matrix[i][j]));
            }
            this.checkingNumbers[j] =  this.coefficient_C[j].subtractionOfCommonFraction(product);
        }


        CommonFraction maxCheckingNumber = checkingNumbers[0];
        positionOfMaxCheckingNumber = 0;//最大检验数的位置
        boolean isZeroInNonbasicVariable0 = false;
        boolean isZeroInNonbasicVariable1 = false;
        for(int j=0; j<COLUMNNUMBER; j++) {

            //看相邻的检验数是否比左边大
            CommonFraction checkingNumber = checkingNumbers[j];
            if(checkingNumber.compare(maxCheckingNumber) > 0) {
                maxCheckingNumber = checkingNumber;
                positionOfMaxCheckingNumber = j;
            }


            //先判断这个检验数是否为0，如果是，再看有没有这个检验数所在列的基变量，由此判断非基变量中是否含0
            if(checkingNumber.compare(ZERO) == 0) {
                //先假设这个 为0的检验数属于非基变量，只要发现这个检验数属于基变量，则让变量为false，最终输出以变量1为准
                isZeroInNonbasicVariable0 = true;
                for(int position : positionsOfBasicVariable) {
                    if(j == position) {
                        isZeroInNonbasicVariable0 = false;
                    }
                }
                if(isZeroInNonbasicVariable0) {
                    isZeroInNonbasicVariable1 = true;
                }
            }
        }

        if(maxCheckingNumber.compare(ZERO) <= 0) {
            boolean isNoSolution = false;
            if(numberOfAddedM > 0) {
                int positionOfFirstM = numberOfInitialVariables + numberOfAddedUnconstrainedVariables + numberOfChangedPositiveSymbols + numberOfChangedNegtiveSymbols;
                for(int i=0; i<LINENUMBER; i++) {
                    for(int j=0; j<numberOfAddedM; j++) {
                        System.out.println("基变量位置："+positionsOfBasicVariable[i]);
                        System.out.println("检验的位置："+(positionOfFirstM + j));
                        if(positionsOfBasicVariable[i] == (positionOfFirstM + j)) {
                            result = "无可行解";
                            isNoSolution = true;
                            break;
                        }
                    }
                    if(result.equals("无可行解")) {
                        break;
                    }
                }
            }
            if(isZeroInNonbasicVariable1 && !isNoSolution) {
                result = "无穷多最优解";
            }
            else if(!isNoSolution) {
                result = "有唯一最优解";
                optimalSolution = new CommonFraction[COLUMNNUMBER];
                //先把所有值赋0，再给基变量赋最优值
                for(int jj=0; jj<COLUMNNUMBER; jj++) {
                    optimalSolution[jj] = ZERO;
                }
                for(int i=0; i<LINENUMBER; i++) {
                    optimalSolution[positionsOfBasicVariable[i]] = matrixAB.matrix[i][COLUMNNUMBER];
                }
                this.optimalValue = new CommonFraction(0);
                for(int jj=0; jj<COLUMNNUMBER; jj++) {
                    this.optimalValue = this.optimalValue.additionOfCommonFraction(coefficient_C[jj].multiplicationOfCommonFraction(optimalSolution[jj]));
                }
                if(!isMax) {
                    this.optimalValue = this.optimalValue.multiplicationOfCommonFraction(NegONE);
                }

            }
        }


        else {
            positionOfMinimunTheta = -1;
            minimumTheta = new CommonFraction(5201314);
            for(int i=0; i<LINENUMBER; i++) {
                CommonFraction divisor = matrixAB.matrix[i][positionOfMaxCheckingNumber];
                //排除被除数和除数等于0的情况
//                if(divisor.getCommonFraction().equals("0") || matrixAB.matrix[i][COLUMNNUMBER].getCommonFraction().equals("0")) {
                if(divisor.getCommonFraction().equals("0") || divisor.compare(ZERO)<0) {
                    coefficient_theta[i] = null;
                    continue;
                }
                CommonFraction theta = matrixAB.matrix[i][COLUMNNUMBER].divisionOfCommnFraction(divisor);
                //θ为负就没有任何意义了
                if(theta.compare(ZERO) < 0) {
                    coefficient_theta[i] = null;
                }
                //存入θ值并记录最小θ的位置
                else {
                    coefficient_theta[i] = theta;
                    if(theta.compare(minimumTheta) < 0 || i==0) {
                        minimumTheta = theta;
                        positionOfMinimunTheta = i;
                    }
                }
            }

            if(positionOfMinimunTheta == -1) {
                result = "无界解";
            }
            else {
                //继续迭代
            }
        }
    }


    //迭代
    public void iteration() {
        positionsOfBasicVariable[positionOfMinimunTheta] = positionOfMaxCheckingNumber;
        for(int i=0; i<LINENUMBER; i++) {
            CommonFraction  divisorI = new CommonFraction(matrixAB.matrix[i][positionsOfBasicVariable[i]].getCommonFraction());
            matrixAB.lineDivision(i, divisorI);
            for(int ii=0; ii<LINENUMBER; ii++) {
                CommonFraction divisorSelf = new CommonFraction(matrixAB.matrix[ii][positionsOfBasicVariable[i]].getCommonFraction());
                if(divisorSelf.getCommonFraction().equals("0")) {
                    continue;
                }
                if(ii != i) {
                    matrixAB.lineMultiplication(i, divisorSelf);
                    matrixAB.lineSubtraction(ii, i);
                    if(matrixAB.matrix[ii][COLUMNNUMBER].compare(ZERO) < 0) {
                        matrixAB.lineMultiplication(ii, -1);
                    }
                    matrixAB.lineDivision(i, divisorSelf);
                }
            }

        }

    }

    //得到基变量的矩阵
    public int[] positionsOfBasicVariable() {
        return positionsOfBasicVariable;
    }

    //得到θ的矩阵
    public CommonFraction[] getTheta() {
        return coefficient_theta;
    }

    //得到检验数的矩阵
    public CommonFraction[] getCheckingNumbers() {
        return checkingNumbers;
    }

    //得到技术系数a的矩阵
    public Matrix getA() {
        Matrix matrixA = this.matrixAB.getSubmatrix(0, 0, this.matrixAB.matrix.length, this.matrixAB.matrix[0].length - 1);
        return matrixA;
    }


    //得到限额系数b的列矩阵
    public Matrix getB() {
        Matrix matrixB = this.matrixAB.getSubmatrix(0, this.matrixAB.matrix[0].length - 1, this.matrixAB.matrix.length, 1);
        return matrixB;
    }


    //更新价值系数
    private void addC(String c) {
        CommonFraction[] newCommonFraction = new CommonFraction[coefficient_C.length + 1];
        newCommonFraction[coefficient_C.length] = new CommonFraction(c);
        for(int j=0; j<coefficient_C.length; j++) {
            newCommonFraction[j] = coefficient_C[j];
        }
        this.coefficient_C = newCommonFraction;
    }


    //插入新的列
    private void insertColumn(int specialPosition, CommonFraction valueOfSpecialPosition, CommonFraction valueOfOtherPosition, String coefficient_C) {
        CommonFraction[] newCommonFraction = new CommonFraction[LINENUMBER];
        for(int i=0; i<LINENUMBER; i++) {
            if(i == specialPosition) {
                newCommonFraction[i] = valueOfSpecialPosition;
            }
            else {
                newCommonFraction[i] = valueOfOtherPosition;
            }
        }
        this.matrixA = this.matrixA.addMatrix(new Matrix(newCommonFraction));
        addC(coefficient_C);
    }
}
