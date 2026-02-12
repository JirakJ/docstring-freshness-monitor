package com.jakubjirak.docfreshness.scanner

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DocBlockScannerTest {

    @Test
    fun `extract Javadoc block from Java file`() {
        val content = """
            package com.example;

            /**
             * This is a Javadoc comment.
             * @param name the name
             */
            public void greet(String name) {
                System.out.println(name);
            }
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "Example.java")

        assertEquals(1, blocks.size)
        assertTrue(blocks[0].text.contains("This is a Javadoc comment"))
        assertEquals(3, blocks[0].lineStart)
        assertEquals(6, blocks[0].lineEnd)
    }

    @Test
    fun `extract KDoc block from Kotlin file`() {
        val content = """
            package com.example

            /**
             * Greets the user by name.
             * @param name the user's name
             */
            fun greet(name: String) {
                println(name)
            }
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "Example.kt")

        assertEquals(1, blocks.size)
        assertTrue(blocks[0].text.contains("Greets the user"))
    }

    @Test
    fun `extract multiple Javadoc blocks`() {
        val content = """
            /**
             * First doc block.
             */
            public class Foo {
                /**
                 * Second doc block.
                 */
                public void bar() {}
            }
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "Foo.java")

        assertEquals(2, blocks.size)
        assertTrue(blocks[0].text.contains("First doc block"))
        assertTrue(blocks[1].text.contains("Second doc block"))
    }

    @Test
    fun `extract Python triple-double docstring`() {
        val content = """
            def greet(name):
                ${"\"\"\""}
                Greets the user by name.

                Args:
                    name: The user's name.
                ${"\"\"\""}
                print(f"Hello, {name}")
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "example.py")

        assertEquals(1, blocks.size)
        assertTrue(blocks[0].text.contains("Greets the user"))
    }

    @Test
    fun `extract Python triple-single docstring`() {
        val content = """
            def hello():
                '''Simple hello function.'''
                print("hello")
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "hello.py")

        assertEquals(1, blocks.size)
        assertTrue(blocks[0].text.contains("Simple hello function"))
    }

    @Test
    fun `extract JSDoc block from TypeScript file`() {
        val content = """
            /**
             * Adds two numbers together.
             * @param a - first number
             * @param b - second number
             * @returns the sum
             */
            function add(a: number, b: number): number {
                return a + b;
            }
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "math.ts")

        assertEquals(1, blocks.size)
        assertTrue(blocks[0].text.contains("Adds two numbers"))
    }

    @Test
    fun `no doc blocks in empty file`() {
        val blocks = DocBlockScanner.scanContent("", "empty.java")
        assertTrue(blocks.isEmpty())
    }

    @Test
    fun `no doc blocks when only regular comments`() {
        val content = """
            // This is a regular comment
            public class Foo {
                /* block comment, not Javadoc */
                public void bar() {}
            }
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "Foo.java")
        assertTrue(blocks.isEmpty())
    }

    @Test
    fun `line numbers are correct for Javadoc`() {
        val content = "line1\nline2\n/**\n * Doc.\n */\ncode"

        val blocks = DocBlockScanner.scanContent(content, "Test.java")

        assertEquals(1, blocks.size)
        assertEquals(3, blocks[0].lineStart)
        assertEquals(5, blocks[0].lineEnd)
    }

    @Test
    fun `extract generic comment blocks`() {
        val content = """
            # This is a comment block
            # that spans multiple lines
            def foo():
                pass
        """.trimIndent()

        val blocks = DocBlockScanner.scanContent(content, "script.sh")

        assertEquals(1, blocks.size)
        assertTrue(blocks[0].text.contains("comment block"))
    }

    @Test
    fun `language detection from file extension`() {
        val javaBlocks = DocBlockScanner.scanContent("/** doc */", "Test.java")
        val pyBlocks = DocBlockScanner.scanContent("\"\"\"doc\"\"\"", "test.py")
        val tsBlocks = DocBlockScanner.scanContent("/** doc */", "test.ts")

        assertEquals(1, javaBlocks.size)
        assertEquals(1, pyBlocks.size)
        assertEquals(1, tsBlocks.size)
    }
}
