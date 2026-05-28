// RevistaDigitalRunning.java
// Una revista interactiva sobre running con artículos, planes de entrenamiento y motivación
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RevistaDigitalRunning {
    
    // Clase para representar un artículo de la revista
    static class Articulo {
        String titulo;
        String autor;
        String contenido;
        String categoria;
        int minutosLectura;
        
        Articulo(String titulo, String autor, String contenido, String categoria, int minutosLectura) {
            this.titulo = titulo;
            this.autor = autor;
            this.contenido = contenido;
            this.categoria = categoria;
            this.minutosLectura = minutosLectura;
        }
        
        void mostrar() {
            System.out.println("\n📖 " + titulo.toUpperCase());
            System.out.println("✍️ Por: " + autor + " | 📚 Categoría: " + categoria + " | ⏱️ " + minutosLectura + " min lectura");
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.println(contenido);
            System.out.println("─────────────────────────────────────────────────────────────");
        }
    }
    
    // Clase para plan de entrenamiento semanal
    static class PlanEntrenamiento {
        String nombre;
        String nivel;
        String[] dias = new String[7];
        
        PlanEntrenamiento(String nombre, String nivel) {
            this.nombre = nombre;
            this.nivel = nivel;
        }
        
        void mostrarPlan() {
            System.out.println("\n🏋️ " + nombre + " - Nivel: " + nivel);
            System.out.println("═══════════════════════════════════════");
            String[] diasSemana = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO"};
            for(int i = 0; i < dias.length; i++) {
                if(dias[i] != null) {
                    System.out.println(diasSemana[i] + " → " + dias[i]);
                }
            }
        }
    }
    
    // Clase para registro del corredor
    static class RegistroRunner {
        String fecha;
        double kmRecorridos;
        int minutos;
        String sensacion;
        String notas;
        
        RegistroRunner(double km, int minutos, String sensacion, String notas) {
            this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            this.kmRecorridos = km;
            this.minutos = minutos;
            this.sensacion = sensacion;
            this.notas = notas;
        }
        
        void mostrar() {
            System.out.println("📅 " + fecha + " | 🏃 " + kmRecorridos + " km | ⏱️ " + minutos + " min | 😊 " + sensacion);
            if(!notas.isEmpty()) System.out.println("   📝 Notas: " + notas);
        }
    }
    
    // Colecciones de datos
    static ArrayList<Articulo> articulos = new ArrayList<>();
    static ArrayList<RegistroRunner> historial = new ArrayList<>();
    static ArrayList<String> frasesMotivadoras = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    
    // Método para inicializar datos de la revista
    static void inicializarRevista() {
        // Artículos de la revista
        articulos.add(new Articulo(
            "Los 10 errores más comunes al empezar a correr",
            "Carlos Méndez, entrenador certificado",
            "1. Aumentar km demasiado rápido\n2. No calentar adecuadamente\n3. Usar zapatillas inadecuadas\n4. Mala hidratación\n5. No respetar días de descanso\n6. Compararse con otros runners\n7. Saltarse los estiramientos\n8. Correr todos los días\n9. Ignorar el dolor\n10. No tener un objetivo claro\n\n🔑 Solución: Empieza despacio, escucha a tu cuerpo y disfruta el proceso.",
            "Consejos", 5
        ));
        
        articulos.add(new Articulo(
            "Cómo preparar tu primer maratón en 16 semanas",
            "Ana Rodríguez, maratonista olímpica",
            "Semana 1-4: Base aeróbica (30-40km/semana)\nSemana 5-8: Aumento de volumen (50-60km)\nSemana 9-12: Series y ritmo (70km)\nSemana 13-15: Tapert (reducción)\nSemana 16: MARATÓN 🏆\n\n💡 Clave: respeta los descansos y la alimentación.",
            "Entrenamiento", 8
        ));
        
        articulos.add(new Articulo(
            "Nutrición para runners: qué comer antes, durante y después",
            "Dra. Laura Fuentes, nutricionista deportiva",
            "🍌 ANTES (2-3h): Carbohidratos complejos (avena, pan integral)\n💧 DURANTE: Hidratación cada 20 min + geles si >90min\n🥗 DESPUÉS (30-60min): Proteína + carbohidratos (batido, fruta, yogur)\n\n🚫 Evitar: comidas pesadas, mucha fibra antes de correr, alimentos nuevos el día de carrera.",
            "Nutrición", 6
        ));
        
        articulos.add(new Articulo(
            "Los mejores destinos para correr en el mundo",
            "Viajes Runner Magazine",
            "🌍 1. Central Park, Nueva York - Urbano y mítico\n🌍 2. Kioto, Japón - Templos y cerezos\n🌍 3. Serengueti, Tanzania - Correr con cebras\n🌍 4. Chamonix, Francia - Montaña y trail\n🌍 5. Valparaíso, Chile - Cerros y vistas al mar\n\n✈️ Planifica tu próxima carrera viajera.",
            "Viajes", 4
        ));
        
        articulos.add(new Articulo(
            "Prevención de lesiones: ejercicios para runner",
            "Fisioterapeuta Javier Torres",
            "🦵 FORTALECIMIENTO:\n- Sentadillas (3x15)\n- Zancadas (3x12 c/pierna)\n- Plancha (3x45 seg)\n- Puente de glúteos (3x15)\n\n🧘 ESTIRAMIENTOS:\n- Cuádriceps\n- Isquiotibiales\n- Gemelos\n- Psoas\n\n📌 Hazlo 2-3 veces/semana.",
            "Salud", 7
        ));
        
        // Frases motivadoras
        frasesMotivadoras.add("💪 'El único mal entrenamiento es el que no se hizo'");
        frasesMotivadoras.add("🏃‍♂️ 'Corremos no porque nos haga daño, sino porque nos hace libres'");
        frasesMotivadoras.add("🌟 'El dolor es temporal, el orgullo es para siempre'");
        frasesMotivadoras.add("🎯 'Las metas no son sueños, son planes en acción'");
        frasesMotivadoras.add("🔥 'Cada amanecer es una nueva oportunidad para superarte'");
        frasesMotivadoras.add("🏆 'No compites contra otros, compites contra tus límites'");
        frasesMotivadoras.add("⚡ 'La única mala carrera es la que no empiezas'");
        frasesMotivadoras.add("🌅 'Correr es meditar con las piernas'");
    }
    
    // Mostrar menú principal
    static void mostrarMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║        🏃‍♂️ RUNNING MAGAZINE - REVISTA DIGITAL 🏃‍♀️       ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║ 1. 📖 Leer artículos de la revista              ║");
        System.out.println("║ 2. 📅 Planes de entrenamiento                    ║");
        System.out.println("║ 3. 📝 Registrar mi entrenamiento                 ║");
        System.out.println("║ 4. 📊 Ver historial de entrenamientos           ║");
        System.out.println("║ 5. 💡 Tip del día + frase motivadora            ║");
        System.out.println("║ 6. 🎽 Calcular ritmo de carrera                  ║");
        System.out.println("║ 7. 📈 Estadísticas personales                   ║");
        System.out.println("║ 8. ❌ Salir                                     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("👉 Elige una opción: ");
    }
    
    // Mostrar artículos
    static void mostrarArticulos() {
        System.out.println("\n📚 ARTÍCULOS DISPONIBLES:");
        System.out.println("═══════════════════════════════════");
        for(int i = 0; i < articulos.size(); i++) {
            System.out.println((i+1) + ". " + articulos.get(i).titulo + " [" + articulos.get(i).categoria + "]");
        }
        System.out.print("\nSelecciona un artículo (0 para cancelar): ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        if(opcion > 0 && opcion <= articulos.size()) {
            articulos.get(opcion-1).mostrar();
            System.out.println("\n📌 ¿Te gustó? ¡Comparte con otros runners!");
        } else if(opcion != 0) {
            System.out.println("❌ Opción inválida");
        }
    }
    
    // Mostrar planes de entrenamiento
    static void mostrarPlanes() {
        System.out.println("\n🏋️ PLANES DE ENTRENAMIENTO");
        System.out.println("1. Principiante (0-3 meses)");
        System.out.println("2. Intermedio (3-6 meses)");
        System.out.println("3. Avanzado (+6 meses)");
        System.out.print("Elige tu nivel: ");
        int nivel = scanner.nextInt();
        scanner.nextLine();
        
        PlanEntrenamiento plan;
        if(nivel == 1) {
            plan = new PlanEntrenamiento("Plan Couch to 5K", "Principiante");
            plan.dias[0] = "Caminata 30 min";
            plan.dias[1] = "Correr 2min + caminar 2min (8x)";
            plan.dias[2] = "Descanso o yoga suave";
            plan.dias[3] = "Correr 3min + caminar 2min (7x)";
            plan.dias[4] = "Descanso";
            plan.dias[5] = "Correr 5min + caminar 3min (5x)";
            plan.dias[6] = "Recuperación activa, caminata 40min";
        } else if(nivel == 2) {
            plan = new PlanEntrenamiento("Plan 10K en 8 semanas", "Intermedio");
            plan.dias[0] = "5km suaves (6:00/km)";
            plan.dias[1] = "Series 6x400m (descanso 90s)";
            plan.dias[2] = "Natación o bicicleta 40min";
            plan.dias[3] = "8km progresivos (6:00 a 5:30/km)";
            plan.dias[4] = "Fuerza 30min + core";
            plan.dias[5] = "10-12km ritmo cómodo";
            plan.dias[6] = "Recuperación 4km suaves";
        } else {
            plan = new PlanEntrenamiento("Plan Media Maratón", "Avanzado");
            plan.dias[0] = "10km suaves + estiramientos";
            plan.dias[1] = "Series 12x400m o 8x800m";
            plan.dias[2] = "15km ritmo medio";
            plan.dias[3] = "Cuestas 10x100m + técnica";
            plan.dias[4] = "Descanso activo o natación 1h";
            plan.dias[5] = "18-21km ritmo de carrera";
            plan.dias[6] = "8km recuperación + yoga";
        }
        plan.mostrarPlan();
    }
    
    // Registrar entrenamiento
    static void registrarEntrenamiento() {
        System.out.println("\n📝 REGISTRO DE ENTRENAMIENTO");
        System.out.print("Kilómetros recorridos: ");
        double km = scanner.nextDouble();
        System.out.print("Minutos totales: ");
        int minutos = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Sensación (Excelente/Bien/Regular/Cansado): ");
        String sensacion = scanner.nextLine();
        System.out.print("Notas adicionales (opcional): ");
        String notas = scanner.nextLine();
        
        RegistroRunner registro = new RegistroRunner(km, minutos, sensacion, notas);
        historial.add(registro);
        System.out.println("\n✅ ¡Entrenamiento guardado! Sigue así 💪");
        
        // Mostrar ritmo
        double ritmo = minutos / km;
        System.out.printf("📊 Ritmo promedio: %.2f min/km\n", ritmo);
        if(ritmo < 5) System.out.println("🏆 ¡Ritmo excelente! Nivel avanzado");
        else if(ritmo < 6) System.out.println("👍 Buen ritmo, sigue entrenando");
        else System.out.println("📈 Ánimo, con constancia mejorarás");
    }
    
    // Ver historial
    static void verHistorial() {
        if(historial.isEmpty()) {
            System.out.println("\n📭 Aún no hay entrenamientos registrados");
            return;
        }
        System.out.println("\n📜 HISTORIAL DE ENTRENAMIENTOS");
        System.out.println("═══════════════════════════════════════");
        double totalKm = 0;
        int totalMin = 0;
        for(RegistroRunner r : historial) {
            r.mostrar();
            totalKm += r.kmRecorridos;
            totalMin += r.minutos;
        }
        System.out.println("\n📊 RESUMEN TOTAL:");
        System.out.println("🏃 Total km: " + totalKm + " km");
        System.out.println("⏱️ Total tiempo: " + totalMin + " minutos (" + (totalMin/60) + " horas)");
        System.out.println("📅 Total entrenamientos: " + historial.size());
    }
    
    // Tip del día
    static void tipDelDia() {
        String[] tips = {
            "🔥 Calienta siempre 10 min antes de correr",
            "💧 Bebe agua cada 20 min durante el running",
            "🦵 Estira después de cada sesión, no antes",
            "👟 Cambia tus zapatillas cada 500-800 km",
            "📈 Aumenta distancia solo 10% por semana",
            "🎽 Usa ropa reflectante si corres de noche",
            "🍌 Come un plátano 30 min antes para energía",
            "💪 Entrena fuerza 2 veces por semana"
        };
        int randomTip = (int)(Math.random() * tips.length);
        int randomFrase = (int)(Math.random() * frasesMotivadoras.size());
        
        System.out.println("\n💡 TIP DEL DÍA:");
        System.out.println("══════════════════════════");
        System.out.println("🏃 " + tips[randomTip]);
        System.out.println("\n🌟 FRASE MOTIVADORA:");
        System.out.println(frasesMotivadoras.get(randomFrase));
    }
    
    // Calcular ritmo
    static void calcularRitmo() {
        System.out.println("\n⏱️ CALCULADORA DE RITMO");
        System.out.print("Distancia (km): ");
        double distancia = scanner.nextDouble();
        System.out.print("Tiempo (minutos): ");
        int minutos = scanner.nextInt();
        scanner.nextLine();
        
        double ritmo = minutos / distancia;
        double velocidad = distancia / (minutos / 60.0);
        
        System.out.printf("\n📊 Ritmo: %.2f min/km\n", ritmo);
        System.out.printf("🚀 Velocidad: %.2f km/h\n", velocidad);
        
        // Equivalencias
        if(ritmo <= 4) System.out.println("🏆 ¡Élite! Ritmo de competencia profesional");
        else if(ritmo <= 5) System.out.println("🔥 Avanzado - Ritmo excelente");
        else if(ritmo <= 6) System.out.println("👍 Intermedio - Vas por buen camino");
        else System.out.println("📈 Principiante - La constancia es la clave");
        
        // Tiempo para otras distancias
        System.out.println("\n🔮 PREDICCIONES:");
        System.out.printf("Tiempo estimado 5K: %.0f min\n", ritmo * 5);
        System.out.printf("Tiempo estimado 10K: %.0f min\n", ritmo * 10);
        System.out.printf("Tiempo estimado 21K (media): %.0f min\n", ritmo * 21);
    }
    
    // Estadísticas personales
    static void mostrarEstadisticas() {
        if(historial.isEmpty()) {
            System.out.println("\n📊 Registra al menos un entrenamiento para ver estadísticas");
            return;
        }
        
        double totalKm = 0;
        double mejorRitmo = Double.MAX_VALUE;
        double peorRitmo = 0;
        int totalDias = historial.size();
        
        for(RegistroRunner r : historial) {
            double ritmo = r.minutos / r.kmRecorridos;
            totalKm += r.kmRecorridos;
            if(ritmo < mejorRitmo) mejorRitmo = ritmo;
            if(ritmo > peorRitmo) peorRitmo = ritmo;
        }
        
        System.out.println("\n📈 ESTADÍSTICAS PERSONALES");
        System.out.println("══════════════════════════════");
        System.out.printf("📊 Total km: %.1f km\n", totalKm);
        System.out.printf("📅 Promedio km/día: %.1f km\n", totalKm / totalDias);
        System.out.printf("🏆 Mejor ritmo: %.2f min/km\n", mejorRitmo);
        System.out.printf("🐢 Peor ritmo: %.2f min/km\n", peorRitmo);
        
        // Logros desbloqueados
        System.out.println("\n🏅 LOGROS DESBLOQUEADOS:");
        if(totalKm >= 42) System.out.println("✓ Maratón completado (42km acumulados) 🎉");
        if(totalKm >= 100) System.out.println("✓ Centenario (100km acumulados) 🌟");
        if(totalDias >= 5) System.out.println("✓ Racha de entrenamiento 🔥");
        if(mejorRitmo < 5.5) System.out.println("✓ Ritmo rápido ⚡");
        if(mejorRitmo < 5) System.out.println("✓ Élite - nivel competitivo 🏆");
        if(totalKm >= 500) System.out.println("✓ Ultra runner legendario 💎");
    }
    
    // Main
    public static void main(String[] args) {
        inicializarRevista();
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   🏃‍♂️ BIENVENIDO A RUNNING MAGAZINE 🏃‍♀️   ║");
        System.out.println("║   Tu revista digital de running        ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        boolean running = true;
        while(running) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine();
            
            switch(opcion) {
                case 1:
                    mostrarArticulos();
                    break;
                case 2:
                    mostrarPlanes();
                    break;
                case 3:
                    registrarEntrenamiento();
                    break;
                case 4:
                    verHistorial();
                    break;
                case 5:
                    tipDelDia();
                    break;
                case 6:
                    calcularRitmo();
                    break;
                case 7:
                    mostrarEstadisticas();
                    break;
                case 8:
                    System.out.println("\n👋 ¡Sigue corriendo! Nos vemos en la próxima edición 🏃‍♂️");
                    running = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida, intenta de nuevo");
            }
        }
        scanner.close();
    }
}