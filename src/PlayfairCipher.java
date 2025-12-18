import java.util.Scanner;

public class PlayfairCipher {
    public static void main(String[] args) {
        int i;
        int j;
        String key;

        Scanner sc = new Scanner(System.in);
        System.out.println("==== Playfair Cipher ====");

        // Citirea cheii de la utilizator
        System.out.print("Introduceti cheia: ");
        key = sc.nextLine();

        // Transformarea cheii in majuscule si eliminarea spatiilor
        key = key.toUpperCase();
        StringBuilder sb_key = new StringBuilder();
        for (i = 0; i < key.length(); i++) {
            if(key.charAt(i) != ' '){
                sb_key.append(key.charAt(i));
            }
        }

        // Eliminarea caracterelor duplicate din cheie
        for (i = sb_key.length() - 1; i >= 0; i--) {
            for (j = 0; j < i; j++) {
                if (sb_key.charAt(j) == sb_key.charAt(i)) {
                    sb_key.deleteCharAt(i);
                    break;
                }
            }
        }

        // Inlocuirea literei J cu I (specific cifrului Playfair)
        for (i = 0; i < sb_key.length(); i++) {
            if(sb_key.charAt(i) == 'J'){
                sb_key.replace(i, i+1, "I");
            }
        }

        System.out.println("\nCheie procesata: " + sb_key + "\n");

        // Crearea si popularea tabelului Playfair 5x5
        char[][] table = new char[5][5];
        String alfabet = "ABCDEFGHIKLMNOPQRSTUVWXYZ"; // Alfabet fara J

        int idx = 0;
        int alfa = 0;
        for(i = 0; i < 5; i++){
            for(j = 0; j < 5; j++){
                if(idx < sb_key.length()){
                    table[i][j] = sb_key.charAt(idx++);
                } else  {
                    // Completarea tabelului cu literele ramase din alfabet
                    if(!sb_key.toString().contains(alfabet.charAt(alfa) + "")){
                        table[i][j] = alfabet.charAt(alfa);
                    } else {
                        j--;
                    }
                    alfa++;
                }
            }
        }

        // Afisarea tabelului Playfair
        System.out.println("Tabela 5x5:");
        for(i = 0; i < 5; i++){
            for(j = 0; j < 5; j++){
                System.out.print(table[i][j]);
            }
            System.out.println();
        }

        // Alegerea modului de lucru
        System.out.println("\nAlege modul:");
        System.out.println("1 - Criptare");
        System.out.println("2 - Decriptare");
        System.out.print("Optiunea ta: ");
        int opt = sc.nextInt();
        sc.nextLine();

        if (opt == 1) {
            // Citirea textului pentru criptare
            System.out.print("Introdu textul ce doresti a fi criptat: ");
            String toEncrypt = sc.nextLine();
            StringBuilder sb_toEncrypt = new StringBuilder();

            // Preprocesarea textului (majuscule, fara spatii)
            toEncrypt = toEncrypt.toUpperCase();
            for(i = 0; i < toEncrypt.length(); i++){
                if(toEncrypt.charAt(i) != ' '){
                    sb_toEncrypt.append(toEncrypt.charAt(i));
                }
            }

            // Inlocuirea literei J cu I
            for(i = 0; i < toEncrypt.length(); i++){
                if(toEncrypt.charAt(i) == 'J'){
                    sb_toEncrypt.replace(i, i+1, "I");
                }
            }

            System.out.println("Textul procesat: " + sb_toEncrypt + "\n");

            // Formarea digramelor si adaugarea caracterelor de umplere
            i = 0;
            while (i < sb_toEncrypt.length()) {
                if (i + 1 < sb_toEncrypt.length() && sb_toEncrypt.charAt(i) == sb_toEncrypt.charAt(i + 1)) {
                    sb_toEncrypt.insert(i + 1, 'X');
                    i += 2;
                } else if (i + 1 < sb_toEncrypt.length()) {
                    i += 2;
                } else {
                    // Completare daca numarul de caractere este impar
                    if(sb_toEncrypt.charAt(i) == 'X'){
                        sb_toEncrypt.append('Z');
                    } else {
                        sb_toEncrypt.append('X');
                    }
                    break;
                }
            }

            // Afisarea digramelor
            System.out.print("Digrame: ");
            for (i = 0; i < sb_toEncrypt.length(); i += 2) {
                System.out.print(sb_toEncrypt.charAt(i));
                System.out.print(sb_toEncrypt.charAt(i + 1) + " ");
            }

            // Criptarea efectiva a textului
            StringBuilder encrypted = new StringBuilder(encrypt(sb_toEncrypt, table));
            System.out.println("\n\nTextul criptat: " + encrypted);

        } else if (opt == 2) {

            // Citirea textului criptat pentru decriptare
            System.out.print("Introdu textul de decriptat: ");
            String ciphertext = sc.nextLine();

            StringBuilder sb_cipher = new StringBuilder(ciphertext.toUpperCase());

            // Decriptarea textului
            StringBuilder decrypted = new StringBuilder(decrypt(sb_cipher, table));
            System.out.println("Textul decriptat (raw): " + decrypted);

            // Eliminarea caracterelor de padding
            String decrypted_withoutPadding = removePadding(decrypted);
            System.out.println("\nTextul decriptat (fara padding): " + decrypted_withoutPadding);

        } else {
            System.out.println("Optiune invalida!");
        }

        sc.close();
    }

    // Metoda pentru criptarea textului folosind regula Playfair
    public static String encrypt(StringBuilder sb_toEncrypt, char[][] table) {
        StringBuilder sb_encrypted = new StringBuilder();

        for (int i = 0; i < sb_toEncrypt.length(); i += 2) {
            char x = sb_toEncrypt.charAt(i);
            char y = sb_toEncrypt.charAt(i + 1);

            int row1 = -1, col1 = -1, row2 = -1, col2 = -1;

            // Cautarea pozitiilor literelor in tabela
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (table[row][col] == x) {
                        row1 = row;
                        col1 = col;
                    }
                    if (table[row][col] == y) {
                        row2 = row;
                        col2 = col;
                    }
                }
            }

            // Aplicarea regulilor Playfair
            if (row1 == row2) {
                sb_encrypted.append(table[row1][(col1 + 1) % 5]);
                sb_encrypted.append(table[row2][(col2 + 1) % 5]);
            } else if (col1 == col2) {
                sb_encrypted.append(table[(row1 + 1) % 5][col1]);
                sb_encrypted.append(table[(row2 + 1) % 5][col2]);
            } else {
                sb_encrypted.append(table[row1][col2]);
                sb_encrypted.append(table[row2][col1]);
            }
        }

        return sb_encrypted.toString();
    }

    // Metoda pentru decriptarea textului
    public static String decrypt(StringBuilder sb_encrypted, char[][] table) {
        StringBuilder sb_decrypted = new StringBuilder();

        for (int i = 0; i < sb_encrypted.length(); i += 2) {
            char x = sb_encrypted.charAt(i);
            char y = sb_encrypted.charAt(i + 1);

            int row1 = -1, col1 = -1, row2 = -1, col2 = -1;

            // Cautarea pozitiilor literelor in tabela
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (table[row][col] == x) {
                        row1 = row;
                        col1 = col;
                    }
                    if (table[row][col] == y) {
                        row2 = row;
                        col2 = col;
                    }
                }
            }

            // Aplicarea regulilor inverse Playfair
            if (row1 == row2) {
                sb_decrypted.append(table[row1][(col1 + 4) % 5]);
                sb_decrypted.append(table[row2][(col2 + 4) % 5]);
            } else if (col1 == col2) {
                sb_decrypted.append(table[(row1 + 4) % 5][col1]);
                sb_decrypted.append(table[(row2 + 4) % 5][col2]);
            } else {
                sb_decrypted.append(table[row1][col2]);
                sb_decrypted.append(table[row2][col1]);
            }
        }

        return sb_decrypted.toString();
    }

    // Eliminarea caracterelor de umplere adaugate la criptare
    public static String removePadding(StringBuilder sb_processed) {
        for (int i = 1; i < sb_processed.length() - 1; i++) {
            if (sb_processed.charAt(i) == 'X' && sb_processed.charAt(i - 1) == sb_processed.charAt(i + 1)) {
                sb_processed.deleteCharAt(i);
            }
        }

        int last = sb_processed.length() - 1;
        if (last >= 1 && (sb_processed.charAt(last) == 'X' || sb_processed.charAt(last) == 'Z')) {
            sb_processed.deleteCharAt(last);
        }

        return sb_processed.toString();
    }
}
