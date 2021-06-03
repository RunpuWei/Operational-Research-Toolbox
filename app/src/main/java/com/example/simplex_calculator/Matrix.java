package com.example.simplex_calculator;

public class Matrix {

    CommonFraction[][] matrix;


    Matrix(){

    }

    Matrix(CommonFraction[][] matrix){
        this.matrix = new CommonFraction[matrix.length][matrix[0].length];
        this.matrix = matrix;
    }


    Matrix(CommonFraction[] matrix){
        this.matrix = new CommonFraction[matrix.length][1];
        for(int i=0; i<matrix.length; i++) {
            this.matrix[i][0] = matrix[i];
        }
    }

    Matrix(int lineNumber, int columnNumber){
        this.matrix = new CommonFraction[lineNumber][columnNumber];
    }

    //行加法
    public void lineAddition(int added, int add) {
        for(int j=0; j<this.matrix[added].length; j++) {
            this.matrix[added][j] = this.matrix[added][j].additionOfCommonFraction(this.matrix[add][j]);
        }
    }


    //行减法
    public void lineSubtraction(int minuend, int subtractor) {
        for(int j=0; j<this.matrix[minuend].length; j++) {
            this.matrix[minuend][j] = this.matrix[minuend][j].subtractionOfCommonFraction(this.matrix[subtractor][j]);
        }
    }


    //行乘法（乘常数）
    public void lineMultiplication(int multiplier, int factor) {
        for(int j=0; j<this.matrix[multiplier].length; j++) {
            this.matrix[multiplier][j] = this.matrix[multiplier][j].multiplicationOfCommonFraction(new CommonFraction(factor));
        }
    }


    //行乘法（乘分数）
    public void lineMultiplication(int multiplier, CommonFraction factor) {
        for(int j=0; j<this.matrix[multiplier].length; j++) {
            this.matrix[multiplier][j] = this.matrix[multiplier][j].multiplicationOfCommonFraction(factor);
        }
    }

    //行除法（除常数）
    public void lineDivision(int dividend, int divisor) {
        for(int j=0; j<this.matrix[dividend].length; j++) {
            this.matrix[dividend][j] = this.matrix[dividend][j].divisionOfCommnFraction(new CommonFraction(divisor));
        }
    }


    //行除法（除分数）
    public void lineDivision(int dividend, CommonFraction divisor) {
        for(int j=0; j<this.matrix[dividend].length; j++) {
            this.matrix[dividend][j] = this.matrix[dividend][j].divisionOfCommnFraction(divisor);
        }
    }


    //矩阵乘法（存在风险）
    public Matrix multiplicationOfMatrix(Matrix matrixR) {
        Matrix newMatrix = new Matrix(new CommonFraction[this.matrix.length][matrixR.matrix[0].length]);
        for(int i=0; i<this.matrix.length; i++) {
            for(int j=0; j<matrixR.matrix[0].length; j++) {
                newMatrix.matrix[i][j] = new CommonFraction(0);
                for(int k=0; k<this.matrix[0].length; k++) {
                    newMatrix.matrix[i][j] = newMatrix.matrix[i][j].additionOfCommonFraction(this.matrix[i][k].multiplicationOfCommonFraction(matrixR.matrix[k][j]));
                }
            }
        }
        return newMatrix;
    }


    //打印矩阵
    public void printMatrix() {
        for(int i=0; i<this.matrix.length; i++) {
            for(int j=0; j<this.matrix[0].length; j++) {
                System.out.printf("%10s", matrix[i][j]);
            }
            System.out.print("\n");
        }
    }

    //扩展矩阵
    public Matrix addMatrix(Matrix matrixR) {
        CommonFraction[][] newCommonFraction = new CommonFraction[this.matrix.length][this.matrix[0].length + matrixR.matrix[0].length];
        for(int i=0; i<matrixR.matrix.length; i++) {
            for(int j=0; j<this.matrix[i].length; j++) {
                newCommonFraction[i][j] = this.matrix[i][j];
            }
            for(int j=0; j<matrixR.matrix[i].length; j++) {
                newCommonFraction[i][j+this.matrix[i].length] = matrixR.matrix[i][j];
            }
        }
        return (new Matrix(newCommonFraction));

    }


    //得到子矩阵 有数组越界风险
    public Matrix getSubmatrix(int TopLeftCornerY, int TopLeftCornerX,  int LengthOfY, int LengthOfX) {
        CommonFraction[][] newCommonFraction = new CommonFraction[LengthOfY][LengthOfX];
        for(int i=0; i<LengthOfY; i++) {
            for(int j=0; j<LengthOfX; j++) {
                newCommonFraction[i][j] = this.matrix[TopLeftCornerY + i][TopLeftCornerX + j];
            }
        }


        Matrix newMatrix = new Matrix(newCommonFraction);
        return newMatrix;
    }
}
