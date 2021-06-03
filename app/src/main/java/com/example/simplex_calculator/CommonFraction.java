package com.example.simplex_calculator;//分数类，整个类的算法实现的分母必为正数，在分子为0时分母的值不确定

public class CommonFraction {
    int denominator;//分母
    int numerator;//分子
    double decimalNumber;//小数
    String commonFraction;//分数形式
    boolean isIllegal = false;


    //分子分母均输入时初始化
    public CommonFraction(int numerator, int denominator){
        if(denominator == 0){
            this.isIllegal = true;
        }
        else{
            if(numerator == 0) {
                this.numerator = numerator;
                this.denominator = denominator;
            }
            else{
                int commonFactor = divisionAlgorithm(numerator, denominator);
                this.numerator = numerator/commonFactor;
                this.denominator = denominator/commonFactor;
            }

            if(this.denominator < 0) {
                this.numerator = -1*this.numerator;
                this.denominator = -1*this.denominator;
            }
        }

    }


    //只输入分子时初始化
    public CommonFraction(int numerator){
        this.numerator = numerator;
        this.denominator = 1;
    }


    //字符串形式输入时初始化
    public CommonFraction(String commonFraction){
        int positionOfSymbol = commonFraction.indexOf("/");
        int positionOfMinusSymbol = commonFraction.indexOf("-");

        //字符以整数形式输入时的转化
        if(positionOfSymbol == -1) {
            this.numerator = stringToInt(commonFraction, 0+positionOfMinusSymbol+1, commonFraction.length()-1);
            if(positionOfMinusSymbol == 0){
                this.numerator *= -1;
            }
            this.denominator = 1;
        }

        //字符以分数形式输入时的转化
        else {
            this.numerator = stringToInt(commonFraction, 0+positionOfMinusSymbol+1, positionOfSymbol-1);
            if(positionOfMinusSymbol == 0) {
                this.numerator *= -1;
            }
            this.denominator = stringToInt(commonFraction, positionOfSymbol+1, commonFraction.length()-1);

            if(this.denominator == 0) {
                this.isIllegal = true;
            } else{
                if(this.numerator != 0){
                    int commonFactor = divisionAlgorithm(numerator, denominator);
                    this.numerator = numerator/commonFactor;
                    this.denominator = denominator/commonFactor;
                }


                if(denominator < 0) {
                    this.numerator = -1*numerator;
                    this.denominator = -1*denominator;
                }
            }
        }
    }


    //如果为非法类，则直接初始化
    public CommonFraction(boolean isIllegal) {
        this.isIllegal = isIllegal;
    }


    //返回分数的字符串
    public String getCommonFraction() {
        if(!isIllegal) {
            if(denominator == 1 || numerator == 0) {
                commonFraction = numerator + "";
            } else {
                commonFraction = numerator+"/"+denominator;
            }
        }
        else {
            commonFraction = "非法值";
        }
        return commonFraction;
    }


    //返回小数的浮点数
    public double getDecimalNumber() {
        decimalNumber = numerator/(1.0*denominator);
        return decimalNumber;
    }


    //乘法
    public CommonFraction multiplicationOfCommonFraction(CommonFraction anotherCommonFraction) {
        CommonFraction newCommonFraction;

        //如果参与计算的数非法均被视为非法，直接返回非法类
        if(this.denominator == 0 || anotherCommonFraction.denominator == 0) {
            newCommonFraction = new CommonFraction(true);
            return newCommonFraction;
        }

        //只要分子有0，直接返回0类
        if(this.numerator == 0 || anotherCommonFraction.numerator == 0) {
            newCommonFraction = new CommonFraction(0);
            return newCommonFraction;
        }


        int commonFactor23 = divisionAlgorithm(this.denominator, anotherCommonFraction.numerator);
        int commonFactor14 = divisionAlgorithm(this.numerator, anotherCommonFraction.denominator);
        int newDenominator = (this.denominator/commonFactor23)*(anotherCommonFraction.denominator/commonFactor14);//约分后的分母
        int newNumerator = (this.numerator/commonFactor14)*(anotherCommonFraction.numerator/commonFactor23);//约分后的分子
        newCommonFraction = new CommonFraction(newNumerator, newDenominator);
        return newCommonFraction;
    }


    //除法
    public CommonFraction divisionOfCommnFraction(CommonFraction anotherCommonFraction) {
        CommonFraction newCommonFraction;
        //如果除了0或参与计算的数非法均被视为非法，直接返回非法类
        if(anotherCommonFraction.numerator == 0 || anotherCommonFraction.denominator == 0 || this.denominator == 0) {
            newCommonFraction = new CommonFraction(true);
            return newCommonFraction;
        }


        //只要分子有0，直接返回0类
        if(this.numerator == 0) {
            newCommonFraction = new CommonFraction(0);
            return newCommonFraction;
        }


        int commonFactor34 = divisionAlgorithm(this.denominator, anotherCommonFraction.denominator);
        int commonFactor12 = divisionAlgorithm(this.numerator, anotherCommonFraction.numerator);
        int newDenominator = (this.denominator/commonFactor34)*(anotherCommonFraction.numerator/commonFactor12);//约分后的分母
        int newNumerator = (this.numerator/commonFactor12)*(anotherCommonFraction.denominator/commonFactor34);//约分后的分子
        newCommonFraction = new CommonFraction(newNumerator, newDenominator);
        return newCommonFraction;
    }


    //加法
    public CommonFraction additionOfCommonFraction(CommonFraction anotherCommonFraction) {
        CommonFraction newCommonFraction;
        //如果参与计算的数非法均被视为非法，直接返回非法类
        if(this.denominator == 0 || anotherCommonFraction.denominator == 0) {
            newCommonFraction = new CommonFraction(true);
            return newCommonFraction;
        }


        int newDenominator = this.denominator*anotherCommonFraction.denominator;
        int newNumerator = (this.numerator*anotherCommonFraction.denominator) + (this.denominator*anotherCommonFraction.numerator);
        if(newNumerator == 0) {
            newCommonFraction = new CommonFraction(0);
            return newCommonFraction;
        }
        int commonFactor = divisionAlgorithm(newDenominator, newNumerator);
        newDenominator /= commonFactor;
        newNumerator /= commonFactor;
        newCommonFraction = new CommonFraction(newNumerator, newDenominator);
        return newCommonFraction;
    }


    //减法
    public CommonFraction subtractionOfCommonFraction(CommonFraction anotherCommonFraction) {
        CommonFraction newCommonFraction;
        //如果参与计算的数非法均被视为非法，直接返回非法类
        if(this.denominator == 0 || anotherCommonFraction.denominator == 0) {
            newCommonFraction = new CommonFraction(true);
            return newCommonFraction;
        }


        int newDenominator = this.denominator*anotherCommonFraction.denominator;
        int newNumerator = (this.numerator*anotherCommonFraction.denominator) + (this.denominator*anotherCommonFraction.numerator*(-1));
        if(newNumerator == 0) {
            newCommonFraction = new CommonFraction(0);
            return newCommonFraction;
        }
        int commonFactor = divisionAlgorithm(newDenominator, newNumerator);
        newDenominator /= commonFactor;
        newNumerator /= commonFactor;
        newCommonFraction = new CommonFraction(newNumerator, newDenominator);
        return newCommonFraction;
    }


    //比较大小
    public int compare(CommonFraction anotherCommonFraction) {
        int ret = 0;
        String result = this.subtractionOfCommonFraction(anotherCommonFraction).getCommonFraction();
        if(result.charAt(0) ==  '-') {
            ret = -1;
        } else if(result.charAt(0) == '0') {
            ret = 0;
        } else {
            ret = 1;
        }
        return ret;
    }


    public void setIllegal(boolean isIllegal) {
        this.isIllegal = isIllegal;
    }


    //辗转相除法
    private int divisionAlgorithm(int a, int b) {
        int c = 1;
        if(a < b) {
            c = a;
            a = b;
            b = c;
        }


        while(true) {
            c = a%b;
            if(c != 0) {
                a = b;
                b = c;
            }
            else {
                break;
            }
        }
        //返回最大公因数
        return b;
    }


    //将字符中的数字转化为int
    private int stringToInt(String commonFraction, int initialPoint, int endPoint) {
        int length = endPoint-initialPoint+1;
        int valueOfInt = 0;
        for(int i=0; i<length; i++) {
            valueOfInt += (commonFraction.charAt(i+initialPoint)-48)*Math.pow(10, length-1-i);
        }
        return valueOfInt;
    }


    //万物皆可toString
    @Override
    public String toString() {
        if(!isIllegal) {
            if(denominator == 1 || numerator == 0) {
                commonFraction = numerator + "";
            } else {
                commonFraction = numerator+"/"+denominator;
            }
        }
        else {
            commonFraction = "非法值";
        }
        return commonFraction;
    }
}
