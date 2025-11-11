package com.farmaciavictoria.proyectopharmavictoria.events;

public interface SystemEventObserver {

    /**
     * Método llamado cuando ocurre un evento en el sistema
     * 
     * @param event El evento que ocurrió
     */
    void onEvent(SystemEvent event);

    /**
     * Identificador único del observador
     * 
     * @return ID único del observador
     */
    String getObserverId();
}