#include <stdio.h>
int main() {
    printf("Hello, World!\n");
    printf("// This is a comment test\n");
    printf("This is a string with a comment // inline comment\n");
    printf("This is a string with /** nested **/ comment\n");
    printf("This is a string with /****/ strange comment pattern\n");
    printf("End of block comment */\n");
    printf(
        "This is a multi-line string/*\
        aaa\
        continued on the next line */\n");
    printf("String with /* inline block comment */ inside\n");
    return 0;
}
