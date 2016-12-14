package ua.myapps.calculatorforpm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.EnumMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextInput;

    private Button buttonDivision;
    private Button buttonMultiplication;
    private Button buttonAddition;
    private Button buttonSubstraction;


    private EnumMap<Symbol, Object> commands = new EnumMap<Symbol, Object>(Symbol.class); //хранит все введенные данные пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = (EditText) findViewById(R.id.editTextInput);

        buttonDivision = (Button) findViewById(R.id.buttonDivision);
        buttonMultiplication = (Button) findViewById(R.id.buttonMultiplication);
        buttonAddition = (Button) findViewById(R.id.buttonAddition);
        buttonSubstraction = (Button) findViewById(R.id.buttonSubstraction);

        // к каждой кнопке добавить тип операции
        buttonDivision.setTag(OperationType.Division);
        buttonMultiplication.setTag(OperationType.Multiplication);
        buttonAddition.setTag(OperationType.Addition);
        buttonSubstraction.setTag(OperationType.Substraction);


    }


    private OperationType operationType;

    private ActionType lastAction;

    @Override
    public void onClick(View v) {

        // определяем нажатую кнопку и выполняем соответствующую операцию
        // в oper пишем операцию, потом будем использовать в выводе
        switch (v.getId()) {

            case R.id.buttonAddition:
            case R.id.buttonSubstraction:
            case R.id.buttonDivision:
            case R.id.buttonMultiplication: {//Кнопка - одна из операций

                operationType = (OperationType) v.getTag();


                if (lastAction == ActionType.OPERATION) {
                    commands.put(Symbol.OPERATION, operationType);
                    return;
                }

                if (!commands.containsKey(Symbol.OPERATION)) {

                    if (!commands.containsKey(Symbol.FIRST_DIGIT)) {
                        commands.put(Symbol.FIRST_DIGIT, editTextInput.getText());
                    }

                    commands.put(Symbol.OPERATION, operationType);
                } else if (!commands.containsKey(Symbol.SECOND_DIGIT)) {
                    commands.put(Symbol.SECOND_DIGIT, editTextInput.getText());
                    doCalc();
                    commands.put(Symbol.OPERATION, operationType);
                    commands.remove(Symbol.SECOND_DIGIT);
                }

                lastAction = ActionType.OPERATION;

                break;

            }

            case R.id.buttonC: {//кнопка очистить
                editTextInput.setText("0");
                commands.clear(); //стереть все введенные команды
                lastAction = ActionType.CLEAR;
                break;
            }

            case R.id.buttonEqually: {

                if (lastAction == ActionType.CALCULATION)
                    return;

                if (commands.containsKey(Symbol.FIRST_DIGIT) && commands.containsKey(Symbol.OPERATION)) {
                    commands.put(Symbol.SECOND_DIGIT, editTextInput.getText());

                    doCalc(); //посчитать
                    commands.clear();

//                    commands.put(Symbol.OPERATION, operationType); //записать тип операции для последующего подсчета
//                    commands.remove(Symbol.SECOND_DIGIT);
                }

                lastAction = ActionType.CALCULATION;

                break;
            }

            case R.id.buttonPoint: {//кнопка для ввода десятичного числа

                if (commands.containsKey(Symbol.FIRST_DIGIT)
                        && getDouble(editTextInput.getText().toString())
                        == getDouble(commands.get(Symbol.FIRST_DIGIT).toString())
                        ) {
                    editTextInput.setText("0" + v.getContentDescription().toString());
                }
                if (!editTextInput.getText().toString().contains(",")) {
                    editTextInput.setText(editTextInput.getText() + ",");
                }

                lastAction = ActionType.POINT;

                break;
            }

            case R.id.buttonDelete: {
                editTextInput.setText(editTextInput.getText()
                        .delete(editTextInput.getText().length() - 1, editTextInput.getText().length()));
                if (editTextInput.getText().toString().trim().length() == 0) {
                    editTextInput.setText("0");
                }

                lastAction = ActionType.DELETE;

                break;
            }

            default: {

                if (editTextInput.getText().toString().equals("0")
                        ||
                        (commands.containsKey(Symbol.FIRST_DIGIT)
                                && getDouble(editTextInput.getText())
                                == getDouble(commands.get(Symbol.FIRST_DIGIT)))
                        || (lastAction == ActionType.CALCULATION)
                        ) {
                    editTextInput.setText(v.getContentDescription().toString());
                } else {
                    editTextInput.setText(editTextInput.getText() + v.getContentDescription().toString());
                }

                lastAction = ActionType.DIGIT;

            }


        }

    }


    private double getDouble(Object value) {
        double result = 0;
        try {
            result = Double.valueOf(value.toString().replace(',', '.')).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }


    private void doCalc() {

        OperationType operationTypeTmp = (OperationType) commands.get(Symbol.OPERATION);

        double result = 0;


        try {


            result = calc(operationTypeTmp,
                    getDouble(commands.get(Symbol.FIRST_DIGIT)),
                    getDouble(commands.get(Symbol.SECOND_DIGIT)));
        } catch (DivisionByZeroException e) {
            showToastMessage(R.string.division_zero);
            return;
        }

        if (result % 1 == 0) {
            editTextInput.setText(String.valueOf((int) result));
        } else {
            editTextInput.setText(String.valueOf(result));
        }
        commands.put(Symbol.FIRST_DIGIT, result);


    }

    private void showToastMessage(int messageID) {
        Toast toastMessage = Toast.makeText(this, messageID, Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.TOP, 0, 100);
        toastMessage.show();
    }

    private Double calc(OperationType operationType, double a, double b) {
        switch (operationType) {
            case Addition: {
                return CalcOperations.addition(a, b);
            }
            case Division: {
                return CalcOperations.division(a, b);
            }
            case Multiplication: {
                return CalcOperations.multiplication(a, b);
            }
            case Substraction: {
                return CalcOperations.substraction(a, b);
            }
        }
        return null;
    }
}

