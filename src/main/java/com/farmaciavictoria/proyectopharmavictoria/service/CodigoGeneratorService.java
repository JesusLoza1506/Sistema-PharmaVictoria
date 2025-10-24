package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 🔧 Servicio para generar códigos automáticos de productos
 * Genera códigos únicos basados en categorías (ANA001, ATB002, etc.)
 */
public class CodigoGeneratorService {
    /**
     * Método público para obtener el prefijo por categoría
     */
    public String getPrefijoPorCategoria(String categoria) {
        return obtenerPrefijoPorCategoria(categoria);
    }

    private static final Logger logger = LoggerFactory.getLogger(CodigoGeneratorService.class);

    // 🏷️ Mapeo de categorías a prefijos de código
    private static final Map<String, String> PREFIJOS_CATEGORIA = new HashMap<>();

    static {
        PREFIJOS_CATEGORIA.put("ANALGESICOS", "ANA");
        PREFIJOS_CATEGORIA.put("ANTIBIOTICOS", "ANT");
        PREFIJOS_CATEGORIA.put("ANTIINFLAMATORIOS", "AIF");
        PREFIJOS_CATEGORIA.put("VITAMINAS", "VIT");
        PREFIJOS_CATEGORIA.put("ANTIACIDOS", "AAC");
        PREFIJOS_CATEGORIA.put("ANTIHISTAMINICOS", "AHT");
        PREFIJOS_CATEGORIA.put("ANTIHIPERTENSIVOS", "AFP");
        PREFIJOS_CATEGORIA.put("DIABETES", "DIA");
        PREFIJOS_CATEGORIA.put("RESPIRATORIO", "RES");
        PREFIJOS_CATEGORIA.put("DERMATOLOGIA", "DER");
        PREFIJOS_CATEGORIA.put("HIGIENE", "HIG");
        PREFIJOS_CATEGORIA.put("OTROS", "OTR");
    }

    /**
     * 🎯 Genera el siguiente código disponible para una categoría
     * 
     * @param categoria La categoría del producto (ej: ANALGESICOS)
     * @return El siguiente código disponible (ej: ANA001, ANA002, etc.)
     */
    public String generarSiguienteCodigo(String categoria) {
        try {
            String prefijo = obtenerPrefijoPorCategoria(categoria);
            int siguienteNumero = obtenerSiguienteNumero(prefijo);
            String codigo = String.format("%s%03d", prefijo, siguienteNumero);

            logger.info("✅ Código generado: {} para categoría: {}", codigo, categoria);
            return codigo;

        } catch (Exception e) {
            logger.error("❌ Error al generar código para categoría {}: {}", categoria, e.getMessage(), e);
            return generarCodigoFallback();
        }
    }

    /**
     * 📋 Obtiene el último código usado para una categoría
     * 
     * @param categoria La categoría del producto
     * @return El último código usado o null si no hay ninguno
     */
    public String obtenerUltimoCodigo(String categoria) {
        try {
            String prefijo = obtenerPrefijoPorCategoria(categoria);

            String sql = "SELECT codigo FROM productos WHERE codigo LIKE ? ORDER BY codigo DESC LIMIT 1";

            try (Connection conn = DatabaseConfig.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, prefijo + "%");

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String ultimoCodigo = rs.getString("codigo");
                        logger.debug("📋 Último código encontrado: {} para prefijo: {}", ultimoCodigo, prefijo);
                        return ultimoCodigo;
                    }
                }
            }

            logger.debug("📋 No se encontraron códigos para prefijo: {}", prefijo);
            return null;

        } catch (Exception e) {
            logger.error("❌ Error al obtener último código para categoría {}: {}", categoria, e.getMessage(), e);
            return null;
        }
    }

    /**
     * ✅ Valida si un código ya existe en la base de datos
     * 
     * @param codigo El código a validar
     * @return true si el código ya existe, false si está disponible
     */
    public boolean codigoExiste(String codigo) {
        try {
            String sql = "SELECT COUNT(*) FROM productos WHERE codigo = ?";

            try (Connection conn = DatabaseConfig.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, codigo.toUpperCase());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        boolean existe = rs.getInt(1) > 0;
                        logger.debug("🔍 Código {} existe: {}", codigo, existe);
                        return existe;
                    }
                }
            }

            return false;

        } catch (Exception e) {
            logger.error("❌ Error al validar código {}: {}", codigo, e.getMessage(), e);
            return true; // Asumir que existe en caso de error por seguridad
        }
    }

    /**
     * 🔤 Obtiene el prefijo de código para una categoría
     */
    private String obtenerPrefijoPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return "GEN"; // General por defecto
        }

        String categoriaUpper = categoria.toUpperCase().trim();

        // 1. Primero intentar desde la base de datos
        String prefijoDb = obtenerPrefijoDesdeBaseDatos(categoriaUpper);
        if (prefijoDb != null) {
            return prefijoDb;
        }

        // 2. Buscar coincidencia exacta en hardcoded
        String prefijo = PREFIJOS_CATEGORIA.get(categoriaUpper);
        if (prefijo != null) {
            return prefijo;
        }

        // 3. Buscar por palabras clave parciales
        for (Map.Entry<String, String> entry : PREFIJOS_CATEGORIA.entrySet()) {
            if (categoriaUpper.contains(entry.getKey()) || entry.getKey().contains(categoriaUpper)) {
                return entry.getValue();
            }
        }

        // 4. Si no encuentra coincidencia, usar las primeras 3 letras
        if (categoriaUpper.length() >= 3) {
            return categoriaUpper.substring(0, 3);
        } else {
            return categoriaUpper + "X".repeat(3 - categoriaUpper.length());
        }
    }

    /**
     * 🗃️ Obtiene el prefijo desde la tabla configuracion_codigos
     */
    private String obtenerPrefijoDesdeBaseDatos(String categoria) {
        try {
            String sql = "SELECT prefijo FROM configuracion_codigos WHERE categoria = ?";

            try (Connection conn = DatabaseConfig.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, categoria);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String prefijo = rs.getString("prefijo");
                        logger.debug("🗃️ Prefijo encontrado en BD: {} para categoría: {}", prefijo, categoria);
                        return prefijo;
                    }
                }
            }

        } catch (Exception e) {
            logger.warn("⚠️ Error al obtener prefijo desde BD para categoría {}: {}", categoria, e.getMessage());
        }

        return null;
    }

    /**
     * 🔢 Obtiene el siguiente número disponible para un prefijo
     */
    private int obtenerSiguienteNumero(String prefijo) throws Exception {
        String sql = "SELECT codigo FROM productos WHERE codigo LIKE ? ORDER BY codigo DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, prefijo + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ultimoCodigo = rs.getString("codigo");

                    // Extraer el número del código (ej: ANA005 -> 5)
                    if (ultimoCodigo.length() > prefijo.length()) {
                        String numeroStr = ultimoCodigo.substring(prefijo.length());

                        try {
                            int ultimoNumero = Integer.parseInt(numeroStr);
                            return ultimoNumero + 1;
                        } catch (NumberFormatException e) {
                            logger.warn("⚠️ No se pudo extraer número del código: {}", ultimoCodigo);
                            return 1;
                        }
                    }
                }

                // No hay códigos con este prefijo, empezar desde 1
                return 1;
            }
        }
    }

    /**
     * 🚨 Genera un código de respaldo en caso de error
     */
    private String generarCodigoFallback() {
        long timestamp = System.currentTimeMillis();
        String fallback = "GEN" + String.valueOf(timestamp).substring(7); // Últimos 6 dígitos
        logger.warn("🚨 Generando código de respaldo: {}", fallback);
        return fallback;
    }

    /**
     * 📝 Obtiene todas las categorías disponibles con sus prefijos
     */
    public Map<String, String> obtenerCategoriasConPrefijos() {
        return new HashMap<>(PREFIJOS_CATEGORIA);
    }

    /**
     * 💡 Sugiere un código basado en el nombre del producto
     */
    public String sugerirCodigoPorNombre(String nombreProducto, String categoria) {
        try {
            // Intentar generar por categoría primero
            if (categoria != null && !categoria.trim().isEmpty()) {
                return generarSiguienteCodigo(categoria);
            }

            // Si no hay categoría, intentar deducir del nombre
            if (nombreProducto != null && !nombreProducto.trim().isEmpty()) {
                String nombreUpper = nombreProducto.toUpperCase();

                // Buscar palabras clave en el nombre del producto
                for (Map.Entry<String, String> entry : PREFIJOS_CATEGORIA.entrySet()) {
                    String categoriaKey = entry.getKey();

                    // Buscar palabras clave comunes
                    if (nombreUpper.contains("PARACETAMOL") || nombreUpper.contains("IBUPROFENO") ||
                            nombreUpper.contains("ASPIRINA") || nombreUpper.contains("ANALGES")) {
                        return generarSiguienteCodigo("ANALGESICOS");
                    }
                    if (nombreUpper.contains("ANTIBIOTIC") || nombreUpper.contains("AMOXICILINA") ||
                            nombreUpper.contains("CIPROFLOXACINO")) {
                        return generarSiguienteCodigo("ANTIBIOTICOS");
                    }
                    if (nombreUpper.contains("VITAMIN")) {
                        return generarSiguienteCodigo("VITAMINAS");
                    }
                    if (nombreUpper.contains("ANTIACID") || nombreUpper.contains("OMEPRAZOL")) {
                        return generarSiguienteCodigo("ANTIACIDOS");
                    }
                }
            }

            // Fallback: generar código genérico
            return generarSiguienteCodigo("OTROS");

        } catch (Exception e) {
            logger.error("❌ Error al sugerir código: {}", e.getMessage(), e);
            return generarCodigoFallback();
        }
    }
}