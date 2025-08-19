package com.github.ivw.ezmode

import com.intellij.openapi.ide.*
import java.awt.datatransfer.*

fun Transferable.getTransferDataOrNull(flavor: DataFlavor) =
  if (isDataFlavorSupported(flavor)) getTransferData(flavor)
  else null

fun getClipboardString(): String? =
  CopyPasteManager.getInstance().contents?.getTransferDataOrNull(DataFlavor.stringFlavor) as? String
