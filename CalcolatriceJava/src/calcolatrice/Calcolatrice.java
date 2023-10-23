package calcolatrice;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Calcolatrice {
    private JPanel panel;
    private JButton value4;
    private JButton value2;
    private JButton equals;
    private JButton value1;
    private JButton value8;
    private JButton value7;
    private JButton value5;
    private JButton moltiplication;
    private JButton addition;
    private JButton value9;
    private JButton value6;
    private JButton value3;
    private JButton value0;
    private JButton valueONOFF;
    private JButton division;
    private JButton subtraction;
    private JButton delete;
    private JButton ac;
    private JButton valueClose;
    private JButton valueOpen;
    private JLabel label;

    private final JButton[]  buttonList = new JButton[]{
            value0, value1, value2, value3, value4, value5, value6, value7, value8,
            value9, addition, subtraction, moltiplication, division,
            valueOpen, valueClose
    };

    public Calcolatrice() {
        final boolean[] isLabelEnabled = new boolean[]{false};
        final boolean[] isLabelPowered = new boolean[]{false};

        for (int i = 0; i < buttonList.length; i++) {
            final int index = i;
            buttonList[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isLabelEnabled[0] && isLabelPowered[0]) {
                        String buttonText = buttonList[index].getText();
                        String currentText = label.getText();

                        char lastCharacter = ' ';

                        if(!currentText.isEmpty()) {
                            lastCharacter = currentText.charAt(currentText.length() - 1);
                        }

                        if(isOperation(buttonText.charAt(0)) || isBrackets(buttonText.charAt(0))){
                            label.setText(currentText + " " + buttonText);
                        }
                        else{
                            if(isOperation(lastCharacter) || isBrackets(lastCharacter)) {
                                label.setText(currentText + " " + buttonText);
                            }
                            else{
                                label.setText(currentText + buttonText);
                            }
                        }
                    }
                }
            });
        }

        valueONOFF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isLabelPowered[0]) {
                    isLabelPowered[0] = true; // Accendo calcolatrice
                    isLabelEnabled[0] = true;
                    label.setText("");
                }
                else{
                    isLabelPowered[0] = false; // Accendo calcolatrice
                    isLabelEnabled[0] = false;
                    label.setText("CALCULATOR OFF [ON/OFF]: Start");
                }
            }
        });
        ac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isLabelPowered[0]) {
                    isLabelEnabled[0] = true;
                    label.setText("");
                }
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentText = label.getText();

                if (!currentText.isEmpty() && isLabelEnabled[0] && isLabelPowered[0]) {
                    // Rimuovi l'ultimo carattere
                    currentText = currentText.substring(0, currentText.length() - 1);

                    // Rimuovi spazi alla fine, se presenti
                    currentText = currentText.replaceAll("\\s+$", "");

                    label.setText(currentText);
                }
            }
        });

        equals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentText = label.getText();
                if (stringaValida(currentText)) {
                    String[] postfissa = calcolaPostfissa(currentText);
                    float risultato = calcolaPreFissa(postfissa);
                    label.setText(Float.toString(risultato));
                }
                else {
                    label.setText("Syntax ERROR [AC]: Cancel");
                    isLabelEnabled[0] = false;
                }
            }
        });
    }
    private boolean stringaValida(String espressione) {

        // Rimuovi gli spazi dalla stringa
        espressione = espressione.replaceAll("\\s+", "");

        if(espressione.isEmpty())
            return false;
        else if (espressione.equals("Syntax ERROR [AC]: Cancel")) {
            return false;
        }

        char carattere;
        int parentesi = 0;
        boolean operatori = false; // Controlla se la stringa ha operatori

        for(int i = 0; i < espressione.length(); i++){
            carattere = espressione.charAt(i);
            if (isOperation(carattere)) {
                if (i == espressione.length() - 1) {
                    return false; // Carattere simbolo all'ultimo indice
                } else if (isOperation(espressione.charAt(i + 1))) {
                    return false; // Due caratteri simbolo consecutivi
                } else if (i == 0) {
                    return false; // Simbolo come primo termine
                }
                operatori = true;
            } else if (carattere == '(') {
                if(i != 0 && !isOperation(espressione.charAt(i-1))){
                    return false; // Operando prima di una parentesi
                }
                parentesi++;
            } else if (carattere == ')') {
                if(parentesi <= 0){
                    return false; // La parentesi non era stata aperta
                } else if (isOperation(espressione.charAt(i-1)) || espressione.charAt(i-1) == '(') {
                    return false; // Trovato simbolo non valido prima della parentesi
                } else if (i != espressione.length()-1 && !isOperation(espressione.charAt(i+1))) {
                    return false; // Trovato operando dopo una parentesi chiusa
                }
                parentesi--;
            }
        }

        if (parentesi != 0) {
            return false; // Ci sono parentesi aperte non chiuse
        }
        if (!operatori) {
            return false; // Non ci sono operatori
        }

        return true;
    }

    private boolean isOperation(char carattere){
        switch (carattere){
            case '+', '-', '*', '/':
                return true;
            default:
                return false;
        }
    }

    private boolean isOperation(String operazione) {
        return operazione.equals("+") || operazione.equals("-") || operazione.equals("*") || operazione.equals("/");
    }
    private boolean isBrackets(char carattere){
        switch (carattere){
            case '(', ')':
                return true;
            default:
                return false;
        }
    }
    private boolean isBrackets(String carattere) {
        return Objects.equals(carattere, "(") || Objects.equals(carattere, ")");
    }

    private static boolean hasHigherOrEqualPrecedence(String op1, String op2) {
        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);

        if (precedence.containsKey(op1) && precedence.containsKey(op2)) {
            return precedence.get(op1) >= precedence.get(op2);
        }

        return false;
    }

    private String[] convertToArray(String infissa) {
        infissa = infissa.replaceAll("\\s+", ""); // Rimuovi tutti gli spazi
        List<String> elements = new ArrayList<>();

        StringBuilder currentElement = new StringBuilder();
        for (int i = 0; i < infissa.length(); i++) {
            char carattere = infissa.charAt(i);
            if (isOperation(carattere) || isBrackets(carattere)) {
                if (!currentElement.isEmpty()) {
                    elements.add(currentElement.toString());
                    currentElement.setLength(0);
                }
                elements.add(String.valueOf(carattere));
            } else {
                currentElement.append(carattere);
            }
        }

        if (!currentElement.isEmpty()) {
            elements.add(currentElement.toString());
        }

        return elements.toArray(new String[0]);
    }
    private String[] calcolaPostfissa(String infissa){

        Stack<String> stack = new Stack<>();
        String[] infissaArray = convertToArray(infissa);

        String[] postFissa = new String[infissaArray.length];
        int index = 0;
        String carattere;

        for(int i = 0; i < infissaArray.length; i++) {
            carattere = infissaArray[i];

            if(!isOperation(carattere) && !isBrackets(carattere)){
                postFissa[index] = carattere; // Se è un operando lo aggiunge al risultato+
                index++;
            }
            else if (isOperation(carattere)) {
                while (!stack.isEmpty() && hasHigherOrEqualPrecedence(stack.peek(), carattere)) {
                    postFissa[index] = stack.pop(); // Se l'operazione prima ha priorità minore o uguale lo aggiunge al risultato
                    index++;
                }
                stack.push(carattere); // Alla fine aggiunge l'operazione
            } else if (carattere.equals("(")) {
                stack.push(carattere);
            } else if (carattere.equals(")")) {
                while (!Objects.equals(stack.peek(), "(")) {
                    postFissa[index] = stack.pop(); // Aggiunge tutte le operazioni finchè non trova la parentesi aperta
                    index++;
                }
                stack.pop(); // Elimina la parentesi aperta
            }
        }

        while (!stack.isEmpty()) {
            postFissa[index] = stack.pop(); // Svuota lo stack e lo aggiunge al risultato
            index++;
        }

        return postFissa;
    }

    private Float calcolaPreFissa(String[] postFissa) {
        Stack<Float> stack = new Stack<>();
        String carattere;

        for (int i = 0; i < postFissa.length && postFissa[i] != null; i++) {
            carattere = postFissa[i];
            if (!isOperation(carattere) && !isBrackets(carattere)) {
                stack.push(Float.parseFloat(carattere));
            } else if (isOperation(carattere)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Espressione non valida");
                }
                float operand2 = stack.pop();
                float operand1 = stack.pop();
                float result = 0;
                switch (carattere) {
                    case "+":
                        result = operand1 + operand2;
                        break;
                    case "-":
                        result = operand1 - operand2;
                        break;
                    case "*":
                        result = operand1 * operand2;
                        break;
                    case "/":
                        if (operand2 == 0) {
                            mathError();
                        }
                        result = operand1 / operand2;
                        break;
                    default:
                        throw new IllegalArgumentException("Operatore non valido");
                }
                stack.push(result);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Espressione non valida");
        }

        return stack.pop();
    }

    private void mathError(){
        label.setText("MATH ERROR [AC]: Cancel");
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Calcolatrice");
        frame.setContentPane(new Calcolatrice().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,650);
        //frame.pack();
        frame.setVisible(true);
    }


}