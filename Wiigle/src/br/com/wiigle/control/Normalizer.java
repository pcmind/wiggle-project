package br.com.wiigle.control;

public class Normalizer {
	/** Para a normalização dos caracteres de 32 a 255, primeiro caracter */
    private static final char[] FIRST_CHAR =
        (" !'#$%&'()*+\\-./0123456789:;<->?@ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ E ,f'.++^%S<O Z  ''''.-"
            + "-~Ts>o ZY !C#$Y|$'(a<--(_o+23'u .,1o>113?AAAAAAACEEEEIIIIDNOO"
            + "OOOXOUUUUyTsaaaaaaaceeeeiiiidnooooo/ouuuuyty")
            .toCharArray();
    /** Para a normalização dos caracteres de 32 a 255, segundo caracter */
    private static final char[] SECOND_CHAR =
        ("  '         ,                                               "
            + "\\                                   $  r'. + o  E      ''  "
            + "  M  e     #  =  'C.<  R .-..     ..>424     E E            "
            + "   E E     hs    e e         h     e e     h ")
            .toCharArray();
    /**
     * Efetua as seguintes normalizações para obtenção de certificados:
     * - Elimina acentos e cedilhas dos nomes;
     * - Converte aspas duplas em simples;
     * - Converte algumas letras estrangeiras para seus equivalentes ASCII
     * (como ß, convertido para ss)
     * - Põe um "\" antes de vírgulas, permitindo nomes como
     * "Verisign, Corp." e de "\", permitindo nomes como " a \ b ";
     * - Converte os sinais de = para -
     * - Alguns caracteres são removidos:
     * -> os superiores a 255,
     * mesmo que possam ser representados por letras latinas normais
     * (como s, = U+015E = Latin Capital Letter S With Cedilla);
     * -> os caracteres de controle (exceto tab, que é trocado por um espaço)
     * @param str A string a normalizar.
     * @return A string normalizada.
     */
    public static String normalize(String str) {
        char[] chars = str.toCharArray();
        StringBuffer ret = new StringBuffer(chars.length * 2);
        for (int i = 0; i < chars.length; ++i) {
            char aChar = chars[i];
            if (aChar == ' ' || aChar == '\t') {
                ret.append(' '); // convertido para espaço
            } else if (aChar > ' ' && aChar < 256) {
                if (FIRST_CHAR[aChar - ' '] != ' ') {
                    ret.append(FIRST_CHAR[aChar - ' ']); // 1 caracter
                }
                if (SECOND_CHAR[aChar - ' '] != ' ') {
                    ret.append(SECOND_CHAR[aChar - ' ']); // 2 caracteres
                }
            }
        }

        return ret.toString();
    }
}
