package com.example.gamevault.core

/**
 * Permite que los Fragments pidan a su Activity contenedora mostrar u
 * ocultar el indicador de carga global.
 */
interface FragmentCommunicator {
    fun manageLoader(isVisible: Boolean)
}
