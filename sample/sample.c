#include <stdio.h>

int main() {
    printf("Hello, World!\n");              // ただの文字列
    printf("// This is a comment test\n");  // コメント記号"//"を含む
    printf("This is a string with a comment // inline comment\n");

    // ブロックコメントのテスト
    printf("This is a string with /** nested **/ comment\n");
    printf("This is a string with /****/ strange comment pattern\n");

    /* ブロックコメントの終了パターン/* */
    printf("End of block comment */\n");

    // 複数行コメント内の文字列テスト
    printf(
        "This is a multi-line string/*\
        aaa\
        continued on the next line */\n");

    printf("String with /* inline block comment */ inside\n");

    /**
     * 複数行コメントのテスト
     * 複数行コメント内での改行も含む
     */

    return 0;
}
