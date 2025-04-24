package com.github.ivw.ezmode

import com.intellij.ide.highlighter.*
import com.intellij.psi.xml.*
import com.intellij.testFramework.*
import com.intellij.testFramework.fixtures.*
import com.intellij.util.*

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

  fun testXMLFile() {
    val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
    val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)

    assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))

    assertNotNull(xmlFile.rootTag)

    xmlFile.rootTag?.let {
      assertEquals("foo", it.name)
      assertEquals("bar", it.value.text)
    }
  }

  fun testRename() {
    myFixture.testRename("foo.xml", "foo_after.xml", "a2")
  }

  override fun getTestDataPath() = "src/test/testData/rename"
}
