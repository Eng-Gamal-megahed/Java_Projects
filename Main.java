import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main
{
    // ***********************
    // Rail‑Fence Cipher Methods
    // ***********************

    // Rail‑Fence encryption (as given)
    static String encryptRailFence(String input)
    {
        if (input == null)
        {
            return "";
        }

        StringBuilder output = new StringBuilder();

        // Process characters in three passes:
        // 1. Append every 4th character starting at index 0.
        // 2. Append every 2nd character starting at index 1.
        // 3. Append every 4th character starting at index 2.
        // (This is a non‑standard rail‑fence cipher approach.)
        for (int i = 0; i < input.length(); i += 4)
        {
            output.append(input.charAt(i));
        }
        for (int i = 1; i < input.length(); i += 2)
        {
            output.append(input.charAt(i));
        }
        for (int i = 2; i < input.length(); i += 4)
        {
            output.append(input.charAt(i));
        }

        return output.toString();
    }

    // Rail‑Fence decryption (as given)
    static String decryptRailFence(String input)
    {
        if (input == null)
        {
            return "";
        }

        // Create a mutable string builder based on the input.
        StringBuilder output = new StringBuilder(input);
        int idx = 0; // This index tracks our position in the cipher text.

        // Reverse the encryption steps by writing characters back into their original positions.
        for (int i = 0; i < input.length(); i += 4)
        {
            output.setCharAt(i, input.charAt(idx++));
        }
        for (int i = 1; i < input.length(); i += 2)
        {
            output.setCharAt(i, input.charAt(idx++));
        }
        for (int i = 2; i < input.length(); i += 4)
        {
            output.setCharAt(i, input.charAt(idx++));
        }

        return output.toString();
    }

    // ***********************
    // Vigenère Cipher Methods
    // ***********************

    // Vigenère encryption
    static String encryptVigenere(String input, String key)
    {
        if (input == null || key == null || key.isEmpty())
        {
            return "";
        }

        // Convert both input and key to uppercase for uniform processing.
        input = input.toUpperCase();
        key = key.toUpperCase();
        StringBuilder output = new StringBuilder();

        // Iterate over each character in the input string.
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            // Encrypt only letters; leave other characters unchanged.
            if (Character.isLetter(c))
            {
                // Calculate the shift: add the key letter's alphabetical index.
                int shift = (c - 'A' + (key.charAt(i % key.length()) - 'A')) % 26;
                output.append((char) (shift + 'A'));
            }
            else
            {
                output.append(c);
            }
        }
        return output.toString();
    }

    // Vigenère decryption
    static String decryptVigenere(String input, String key)
    {
        if (input == null || key == null || key.isEmpty())
        {
            return "";
        }

        // Convert input and key to uppercase.
        input = input.toUpperCase();
        key = key.toUpperCase();
        StringBuilder output = new StringBuilder();

        // Iterate over each character and reverse the encryption process.
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            if (Character.isLetter(c))
            {
                // Reverse the shift applied during encryption.
                int shift = (c - 'A' - (key.charAt(i % key.length()) - 'A') + 26) % 26;
                output.append((char) (shift + 'A'));
            }
            else
            {
                output.append(c);
            }
        }
        return output.toString();
    }

    // ***********************
    // Baconian Cipher Methods
    // ***********************

    /**
     * Baconian encryption:
     * Converts each character into a sequence of 5 letters ('a' or 'b').
     * The algorithm uses the 5 least‑significant bits of the character's integer value.
     * For each bit (from bit 0 to bit 4):
     *   - If the bit is set (1), append 'a'.
     *   - If the bit is not set (0), append 'b'.
     * Spaces in the original text are preserved.
     */
    static String encryptBaconian(String input)
    {
        if (input == null)
        {
            return "";
        }

        StringBuilder output = new StringBuilder();

        // Process each character from the input string.
        for (int i = 0; i < input.length(); i++)
        {
            // If the character is a space, append it directly.
            if (input.charAt(i) == ' ')
            {
                output.append(' ');
                continue;
            }
            // For each non‑space character, process 5 bits (from LSB to bit 4).
            for (int j = 0; j < 5; j++)
            {
                // Check if the j‑th bit is set using bitwise AND.
                // If set, append 'a'; otherwise, append 'b'.
                if ((input.charAt(i) & (1 << j)) != 0)
                {
                    output.append('a');
                }
                else
                {
                    output.append('b');
                }
            }
        }
        return output.toString();
    }

    /**
     * Baconian decryption:
     * Reverses the Baconian encryption by reading every group of 5 characters (ignoring spaces)
     * and converting them back to the original character.
     *
     * Each 5‑letter group represents the 5 least‑significant bits of a character.
     * The first letter of the group corresponds to bit 0 (the least significant bit)
     * and the fifth letter corresponds to bit 4.
     *
     * The method also preserves spaces that appear in the cipher text.
     */
    static String decryptBaconian(String input)
    {
        if (input == null)
        {
            return "";
        }

        StringBuilder output = new StringBuilder();
        int i = 0; // Index for traversing the cipher text

        // Process the input string until all characters are handled.
        while (i < input.length())
        {
            // If the current character is a space, it represents a space in the original text.
            if (input.charAt(i) == ' ')
            {
                output.append(' ');
                i++; // Move to the next character
            }
            else
            {
                // Ensure there are at least 5 characters remaining for a complete Baconian block.
                if (i + 5 > input.length())
                {
                    // Incomplete block detected; break out of the loop.
                    break;
                }

                int value = 0; // This will store the numeric value (0‑31) of the decoded character.

                // Process the next 5 characters which form one block.
                // Each letter in the block determines one bit of the character.
                // The first letter corresponds to bit 0 (least significant) and the fifth to bit 4.
                for (int j = 0; j < 5; j++)
                {
                    char cipherChar = input.charAt(i + j);

                    // If the cipher character is 'a', set the j‑th bit (bit value 1).
                    if (cipherChar == 'a')
                    {
                        value |= (1 << j); // Set bit j
                    }
                    // If the character is 'b', the bit remains 0.
                    // You might add error handling for unexpected characters if desired.
                }

                // Convert the numeric value back to a character.
                char decodedChar = (char) value;
                output.append(decodedChar);

                // Move the index forward by 5, as we have processed this block.
                i += 5;
            }
        }
        return output.toString();
    }

    // ***********************
    // Affine Cipher Methods
    // ***********************

    /**
     * Affine encryption:
     * Encrypts the input text by applying the function: E(x) = (a * x + b) mod 26.
     * 'a' and 'b' are parameters of the cipher, and it only processes letters.
     * The parameter 'c' is the multiplicative inverse of 'a' modulo 26 and is used during decryption.
     */
    static String encryptAffine(String input, int a, int b, int c)
    {
        if (input == null)
        {
            return "";
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            char c1 = input.charAt(i);
            if (Character.isLetter(c1))
            {
                int x = c1 - 'A';
                int y = (a * x + b) % 26;
                output.append((char) (y + 'A'));
            }
            else
            {
                output.append(c1);
            }
        }
        return output.toString();
    }

    /**
     * Affine decryption:
     * Decrypts the input text by reversing the affine encryption:
     * D(y) = c * (y - b) mod 26, where 'c' is the multiplicative inverse of 'a' modulo 26.
     * Only letters are processed.
     */
    static String decryptAffine(String input, int a, int b, int c)
    {
        if (input == null)
        {
            return "";
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            char c1 = input.charAt(i);
            if (Character.isLetter(c1))
            {
                int y = c1 - 'A';
                int x = c * (y - b);
                // Adjust for potential negative values before taking modulo 26.
                x = (x % 26 + 26) % 26;
                output.append((char) (x + 'A'));
            }
            else
            {
                output.append(c1);
            }
        }
        return output.toString();
    }

    // ***********************
    // Morse Code Cipher Methods
    // ***********************

    /**
     * Morse Code encryption:
     * Converts each alphanumeric character to its Morse code equivalent.
     * Letters and digits are translated based on a standard Morse code mapping.
     * Each Morse code letter is separated by a space, and words are separated by " / ".
     * Non-alphanumeric characters (other than space) are ignored.
     */
    static String encryptMorseCode(String input)
    {
        if (input == null)
        {
            return "";
        }

        // Define Morse code mapping for letters and digits.
        java.util.Map<Character, String> morseMap = new java.util.HashMap<>();
        morseMap.put('A', ".-");
        morseMap.put('B', "-...");
        morseMap.put('C', "-.-.");
        morseMap.put('D', "-..");
        morseMap.put('E', ".");
        morseMap.put('F', "..-.");
        morseMap.put('G', "--.");
        morseMap.put('H', "....");
        morseMap.put('I', "..");
        morseMap.put('J', ".---");
        morseMap.put('K', "-.-");
        morseMap.put('L', ".-..");
        morseMap.put('M', "--");
        morseMap.put('N', "-.");
        morseMap.put('O', "---");
        morseMap.put('P', ".--.");
        morseMap.put('Q', "--.-");
        morseMap.put('R', ".-.");
        morseMap.put('S', "...");
        morseMap.put('T', "-");
        morseMap.put('U', "..-");
        morseMap.put('V', "...-");
        morseMap.put('W', ".--");
        morseMap.put('X', "-..-");
        morseMap.put('Y', "-.--");
        morseMap.put('Z', "--..");
        morseMap.put('0', "-----");
        morseMap.put('1', ".----");
        morseMap.put('2', "..---");
        morseMap.put('3', "...--");
        morseMap.put('4', "....-");
        morseMap.put('5', ".....");
        morseMap.put('6', "-....");
        morseMap.put('7', "--...");
        morseMap.put('8', "---..");
        morseMap.put('9', "----.");

        StringBuilder output = new StringBuilder();
        input = input.toUpperCase();

        // Process each character in the input string.
        for (int i = 0; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            // If character is a space, denote word separation with " / "
            if (ch == ' ')
            {
                output.append(" / ");
            }
            else if (morseMap.containsKey(ch))
            {
                output.append(morseMap.get(ch));
                output.append(" "); // separate letters by space
            }
            // For non-alphanumeric characters, ignore them.
        }
        return output.toString().trim();
    }

    /**
     * Morse Code decryption:
     * Reverses the Morse Code encryption by converting Morse code sequences back to alphanumeric characters.
     * Assumes that individual Morse code letters are separated by spaces and words are separated by " / ".
     * Unknown Morse sequences are ignored.
     */
    static String decryptMorseCode(String input)
    {
        if (input == null)
        {
            return "";
        }

        // Define reverse mapping for Morse code to letters and digits.
        java.util.Map<String, Character> morseToChar = new java.util.HashMap<>();
        morseToChar.put(".-", 'A');
        morseToChar.put("-...", 'B');
        morseToChar.put("-.-.", 'C');
        morseToChar.put("-..", 'D');
        morseToChar.put(".", 'E');
        morseToChar.put("..-.", 'F');
        morseToChar.put("--.", 'G');
        morseToChar.put("....", 'H');
        morseToChar.put("..", 'I');
        morseToChar.put(".---", 'J');
        morseToChar.put("-.-", 'K');
        morseToChar.put(".-..", 'L');
        morseToChar.put("--", 'M');
        morseToChar.put("-.", 'N');
        morseToChar.put("---", 'O');
        morseToChar.put(".--.", 'P');
        morseToChar.put("--.-", 'Q');
        morseToChar.put(".-.", 'R');
        morseToChar.put("...", 'S');
        morseToChar.put("-", 'T');
        morseToChar.put("..-", 'U');
        morseToChar.put("...-", 'V');
        morseToChar.put(".--", 'W');
        morseToChar.put("-..-", 'X');
        morseToChar.put("-.--", 'Y');
        morseToChar.put("--..", 'Z');
        morseToChar.put("-----", '0');
        morseToChar.put(".----", '1');
        morseToChar.put("..---", '2');
        morseToChar.put("...--", '3');
        morseToChar.put("....-", '4');
        morseToChar.put(".....", '5');
        morseToChar.put("-....", '6');
        morseToChar.put("--...", '7');
        morseToChar.put("---..", '8');
        morseToChar.put("----.", '9');

        StringBuilder output = new StringBuilder();
        // Split the input by space to get individual Morse code tokens.
        String[] tokens = input.trim().split("\\s+");

        for (String token : tokens)
        {
            // If token is "/" it represents a space between words.
            if (token.equals("/"))
            {
                output.append(" ");
            }
            else if (morseToChar.containsKey(token))
            {
                output.append(morseToChar.get(token));
            }
            // Unknown tokens are ignored.
        }

        return output.toString();
    }

    // ***********************
    // Main method: User Interface and File Processing
    // ***********************

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to my cipher solver");
        System.out.print("Please enter file to be decrypted/encrypted: ");
        String filename = scanner.nextLine().trim();
        File inputFile = new File(filename);

        // Validate that the file exists and is a valid file.
        while (!inputFile.exists() || !inputFile.isFile())
        {
            System.out.println("The file '" + filename + "' does not exist or is not a valid file. Please check the file path.");
            System.out.print("Enter file name again: ");
            filename = scanner.nextLine().trim();
            inputFile = new File(filename);
        }

        // Display available cipher options.
        System.out.println("(1) Rail‑Fence Cipher");
        System.out.println("(2) Vigenère Cipher");
        System.out.println("(3) Baconian Cipher");
        System.out.println("(4) Affine Cipher");
        System.out.println("(5) Morse Code");
        System.out.print("Choose cipher type (1, 2, 3, 4, or 5): ");
        String cipherChoice = scanner.next().trim();
        scanner.nextLine(); // Consume newline

        // Check if a valid cipher option was selected.
        if (!cipherChoice.equals("1") && !cipherChoice.equals("2") &&
                !cipherChoice.equals("3") && !cipherChoice.equals("4") && !cipherChoice.equals("5"))
        {
            System.out.println("Invalid cipher type selected. Exiting.");
            scanner.close();
            return;
        }

        System.out.print("Would you like to (1) encrypt or (2) decrypt? ");
        String action = scanner.next().trim();
        scanner.nextLine(); // Consume newline

        // Validate the action choice.
        if (!action.equals("1") && !action.equals("2"))
        {
            System.out.println("Invalid action selected. Exiting.");
            scanner.close();
            return;
        }

        String key = "";
        // Only the Vigenère cipher requires a key.
        if (cipherChoice.equals("2"))
        {
            System.out.print("Enter key for Vigenère cipher: ");
            key = scanner.nextLine().trim();

            // Ensure the key is not empty.
            while (key.isEmpty())
            {
                System.out.print("Key cannot be empty. Enter key for Vigenère cipher: ");
                key = scanner.nextLine().trim();
            }
        }

        // Set default parameters for Affine cipher.
        int a = 5, b = 8, c = 21;
        if (cipherChoice.equals("4"))
        {
            System.out.println("Enter parameters for affine cipher (a, b, c) or leave blank for defaults (5, 8, 21).");
            System.out.println("Note that (a * c) mod 26 must equal 1 !");
            System.out.print("Enter a: ");
            a = scanner.nextInt();
            System.out.print("Enter b: ");
            b = scanner.nextInt();
            System.out.print("Enter c: ");
            c = scanner.nextInt();
            // Validate parameters: (a * c) mod 26 must equal 1.
            while ((a * c) % 26 != 1)
            {
                System.out.println("Invalid parameters. Try again.");
                System.out.println("Note that (a * c) mod 26 must equal 1 !");
                System.out.print("Enter a: ");
                a = scanner.nextInt();
                System.out.print("Enter b: ");
                b = scanner.nextInt();
                System.out.print("Enter c: ");
                c = scanner.nextInt();
            }
            scanner.nextLine(); // Consume the newline after reading integers
        }

        // Process the input file and write the results to "Processing_File.txt".
        try (Scanner fileScanner = new Scanner(inputFile);
             FileWriter writer = new FileWriter("Processing_File.txt"))
        {
            // Process the file line by line.
            while (fileScanner.hasNextLine())
            {
                String line = fileScanner.nextLine();
                String processedLine = "";

                // Choose the cipher based on user selection.
                if (cipherChoice.equals("1"))
                {
                    // Rail‑Fence Cipher
                    if (action.equals("1"))
                    {
                        // Encrypt using Rail‑Fence cipher.
                        processedLine = encryptRailFence(line);
                    }
                    else
                    {
                        // Decrypt using Rail‑Fence cipher.
                        processedLine = decryptRailFence(line);
                    }
                }
                else if (cipherChoice.equals("2"))
                {
                    // Vigenère Cipher
                    if (action.equals("1"))
                    {
                        // Encrypt using Vigenère cipher.
                        processedLine = encryptVigenere(line, key);
                    }
                    else
                    {
                        // Decrypt using Vigenère cipher.
                        processedLine = decryptVigenere(line, key);
                    }
                }
                else if (cipherChoice.equals("3"))
                {
                    // Baconian Cipher
                    if (action.equals("1"))
                    {
                        // Encrypt using Baconian cipher.
                        processedLine = encryptBaconian(line);
                    }
                    else
                    {
                        // Decrypt using Baconian cipher.
                        processedLine = decryptBaconian(line);
                    }
                }
                else if (cipherChoice.equals("4"))
                {
                    // Affine Cipher
                    if (action.equals("1"))
                    {
                        // Encrypt using Affine cipher.
                        processedLine = encryptAffine(line, a, b, c);
                    }
                    else
                    {
                        // Decrypt using Affine cipher.
                        processedLine = decryptAffine(line, a, b, c);
                    }
                }
                else if (cipherChoice.equals("5"))
                {
                    // Morse Code
                    if (action.equals("1"))
                    {
                        // Encrypt using Morse Code.
                        processedLine = encryptMorseCode(line);
                    }
                    else
                    {
                        // Decrypt using Morse Code.
                        processedLine = decryptMorseCode(line);
                    }
                }

                // Write the processed line to the output file, appending a newline.
                writer.write(processedLine + System.lineSeparator());
            }

            System.out.println("Operation completed. Processed file saved as Processing_File.txt");
        }
        catch (IOException e)
        {
            System.err.println("Error processing the file: " + e.getMessage());
        }

        scanner.close();
    }
}
