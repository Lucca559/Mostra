package exercicio08;

import java.util.Scanner;

public class questao6 {
    public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);

        System.out.print("Digite um número inteiro positivo para ver o termo de Fibonacci: ");
        int n = scanner.nextInt();

        if (n < 0) {
            System.out.println("Número inválido. Por favor, digite um inteiro positivo.");
        } else {
            int termoFibonacci = calcularFibonacci(n);
            System.out.println("O termo de número " + n + " na sequência de Fibonacci é: " + termoFibonacci);
        }

        scanner.close();
    }

    public static int calcularFibonacci(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;

        int a = 0, b = 1, resultado = 0;

        for (int i = 2; i <= n; i++) {
            resultado = a + b;
            a = b;
            b = resultado;
        }

        return resultado;
    }
}
    

