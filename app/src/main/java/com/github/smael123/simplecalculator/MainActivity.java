package com.github.smael123.simplecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Stack;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private TextView calculatorInput;

    private Button[] numberButtons;

    private Button divideButton;
    private Button multiplyButton;
    private Button subtractButton;
    private Button addButton;

    private Button backspaceButton;
    private Button clearButton;

    private Button decimalButton;

    private Button equalsButton;

    private Button polarityButton;

    private final String negativeChar = "-";

    private final String [] operatorArray = {"/", "*", "−", "+"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculatorInput = (TextView)findViewById(R.id.calculatorInput);

        backspaceButton = (Button)findViewById(R.id.backspaceButton);
        backspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLastCharacter(view);
            }
        });

        clearButton = (Button)findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                clearCalculatorInput(view);
            }
        });

        numberButtons = new Button[10];

        numberButtons[0] = (Button)findViewById(R.id.zeroButton);
        numberButtons[1] = (Button)findViewById(R.id.oneButton);
        numberButtons[2] = (Button)findViewById(R.id.twoButton);
        numberButtons[3] = (Button)findViewById(R.id.threeButton);
        numberButtons[4] = (Button)findViewById(R.id.fourButton);
        numberButtons[5] = (Button)findViewById(R.id.fiveButton);
        numberButtons[6] = (Button)findViewById(R.id.sixButton);
        numberButtons[7] = (Button)findViewById(R.id.sevenButton);
        numberButtons[8] = (Button)findViewById(R.id.eightButton);
        numberButtons[9] = (Button)findViewById(R.id.nineButton);

        for (int i = 0; i < 10; i++){
            final int tempIndex = i;
            numberButtons[i].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    inputNumber(view, tempIndex);
                }
            });
        }

        divideButton = (Button)findViewById(R.id.divideButton);
        divideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                calculatorInput.append("/");
            }
        });

        multiplyButton = (Button)findViewById(R.id.multiplyButton);
        multiplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                calculatorInput.append("*");
            }
        });

        subtractButton = (Button)findViewById(R.id.subtractButton);
        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                calculatorInput.append("−");
            }
        });

        addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                calculatorInput.append("+");
            }
        });

        decimalButton = (Button)findViewById(R.id.decimalButton);
        decimalButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String input = calculatorInput.getText().toString();
                input = input.trim() + ".";

                calculatorInput.setText(input);
            }
        });

        equalsButton = (Button)findViewById(R.id.equalsButton);
        equalsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ArrayDeque<String> postfixString = convertToPostfix(calculatorInput.getText().toString());

                if (postfixString != null)
                    calculatorInput.setText(Double.toString(calculate(postfixString)));
                else
                    calculatorInput.setText(R.string.errorToken);
            }
        });

        polarityButton = (Button)findViewById(R.id.polarityButton);
        polarityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                calculatorInput.append(negativeChar);
            }
        });
    }

    private void removeLastCharacter(View v){
        String input = calculatorInput.getText().toString();
        input = input.trim();
        int len = input.length();

        try{
            calculatorInput.setText(input.substring(0, len-1));
        }
        catch (IndexOutOfBoundsException oobe){
            clearCalculatorInput(v);
        }
    }

    private void clearCalculatorInput(View v){
        calculatorInput.setText("");
    }

    private void inputNumber(View v, int num){
        calculatorInput.append(Integer.toString(num));
    }

    private ArrayDeque<String> convertToPostfix(String input){
        String [] inputArray = splitInput(input);
        ArrayDeque<String> postfixString = new ArrayDeque<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : inputArray){
            if (isNumber(token)){
                postfixString.add(token);
            }
            else if (isOperator(token)){
                while (!operatorStack.empty()){
                    String top = operatorStack.peek();
                    if (getPrecedence(token, top) <= 0){
                        postfixString.add(operatorStack.pop());
                    }
                    else {
                        break;
                    }
                }
                operatorStack.push(token);
            }
            else{
                //raise exception
                return null;
            }
        }

        while (!operatorStack.empty()){
            postfixString.add(operatorStack.pop());
        }

        return postfixString;
    }



    private boolean isOperator(String token){
        for (String operator : operatorArray){
            if (token.equals(operator)){
                return true;
            }
        }

        return false;
    }

    private boolean isNumber(String str){
        try{
            Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException nfe){
            return false;
        }
    }

    private int getPrecedence(String operator, String top){
        //assumes only * / + - are going to be passed in
        //returns the precedence of the operator compared to the top

        if (operator.equals("+") || operator.equals("−")){
            if (top.equals("+") || top.equals("−")){
                return 0;
            }
            else{
                return -1;
            }
        }
        else {
            if (top.equals("+") || top.equals("−")){
                return 1;
            }
            else{
                return 0;
            }
        }
    }

    private double calculate(ArrayDeque<String> postfixString){
        Stack<String> operandStack = new Stack<>();

        while (!postfixString.isEmpty()){
            String token = postfixString.remove();
            if (isNumber(token)){
                operandStack.push(token);
            }
            else if (isOperator(token)){
                double op2 = Double.parseDouble(operandStack.pop());
                double op1 = Double.parseDouble(operandStack.pop());

                if (token.equals("/")){
                    operandStack.push(Double.toString(op1 / op2));
                }
                else if (token.equals("*")){
                    operandStack.push(Double.toString(op1 * op2));
                }
                else if (token.equals("−")){
                    operandStack.push(Double.toString(op1 - op2));
                }
                else if (token.equals("+")){
                    operandStack.push(Double.toString(op1 + op2));
                }
            }
        }

        return Double.parseDouble(operandStack.pop());
    }

    //splits an input into tokens and returns an array of strings with said tokens
    private String [] splitInput (String input){
        StringTokenizer st = new StringTokenizer(input, convertStringArrayToString(operatorArray), true);
        int len = st.countTokens();

        //CAN'T DO THIS IN C++
        String [] inputArray = new String[len];

        for (int i = 0; i < len; i++){
            inputArray[i] = st.nextToken();
        }

        return inputArray;
    }

    private String convertStringArrayToString (String [] strArr){
        int len = strArr.length;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++){
            sb.append(strArr[i]);
        }

        return sb.toString();
    }
}