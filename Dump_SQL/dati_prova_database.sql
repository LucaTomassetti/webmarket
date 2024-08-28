/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

INSERT INTO `categories` (`id`, `name`) VALUES
	(2, 'Laptop'),
	(4, 'Arredamento'),
	(7, 'TV'),
	(8, 'Smart Home'),
	(9, 'Altoparlanti'),
	(10, 'Networking');

INSERT INTO `characteristics` (`characteristic_id`, `name`, `subcategory_id`) VALUES
	(27, 'Dimensioni', 8),
	(28, 'Colore', 8),
	(29, 'Tipo di luce', 9),
	(30, 'Colore', 9),
	(43, 'Connessione', 16),
	(44, 'Compatibilità con assistenti vocali', 16),
	(45, 'Risoluzione', 16),
	(46, 'Archiviazione cloud', 16),
	(47, 'Controllo remoto', 17),
	(48, 'Sensori di temperatura', 17),
	(49, 'Compatibilità', 17),
	(50, 'Programmazione', 17),
	(51, 'Potenza', 18),
	(52, 'Connessioni', 18),
	(53, 'Compatibilità con assistenti vocali', 18),
	(54, 'Durata batteria', 18),
	(55, 'Qualità del suono', 18),
	(56, 'Potenza', 19),
	(57, 'Connessioni', 19),
	(58, 'Compatibilità con assistenti vocali', 19),
	(59, 'Durata batteria', 19),
	(60, 'Qualità del suono', 19),
	(61, 'Velocità', 20),
	(62, 'Frequenza', 20),
	(63, 'Tecnologia MIMO', 20),
	(64, 'Porta Ethernet', 20),
	(65, 'Dimensione', 21),
	(66, 'Risoluzione', 21),
	(67, 'Dimensione', 22),
	(68, 'Risoluzione', 22),
	(69, 'HDR', 22),
	(70, 'Frequenza di aggiornamento', 22),
	(71, 'Sistema operativo', 22),
	(72, 'RAM', 23),
	(73, 'Processore', 23),
	(74, 'Memoria', 23),
	(75, 'Schermo', 23),
	(76, 'RAM', 24),
	(77, 'Processore', 24),
	(78, 'Memoria', 24),
	(79, 'Schermo', 24);

INSERT INTO `purchaserequests` (`id`, `createdAt`, `notes`, `status`, `user_id`, `subcategory_id`) VALUES
	(1, '2024-08-27 23:06:38.000000', 'Alimentatore Corsair', 'PENDING', 33, 24),
	(2, '2024-08-27 23:15:15.000000', '', 'PENDING', 33, 20),
	(3, '2024-08-27 23:45:57.000000', 'Deve ancorarsi al bordo della scrivania', 'PENDING', 33, 9);

INSERT INTO `requestcharacteristics` (`id`, `value`, `characteristic_id`, `purchase_request_id`) VALUES
	(1, '8 GB', 76, 1),
	(2, 'Octa-Core 3GHz', 77, 1),
	(3, '1 TB', 78, 1),
	(4, 'indifferente', 79, 1),
	(5, 'indifferente', 61, 2),
	(6, '5GHz', 62, 2),
	(7, 'indifferente', 63, 2),
	(8, '1GBit', 64, 2),
	(9, 'Bianca', 29, 3),
	(10, 'indifferente', 30, 3);

INSERT INTO `subcategories` (`id`, `name`, `category_id`) VALUES
	(8, 'Scrivania', 4),
	(9, 'Lampada', 4),
	(16, 'Telecamere', 8),
	(17, 'Condizionatori smart', 8),
	(18, 'Bluetooth', 9),
	(19, 'Soundbar', 9),
	(20, 'Extender', 10),
	(21, 'OLED', 7),
	(22, '4K', 7),
	(23, 'Notebook', 2),
	(24, 'PC fisso', 2);

INSERT INTO `users` (`userID`, `email`, `password`, `userType`, `username`) VALUES
	(1, 'admin@gmail.com', '$2a$10$OeHfeInIAlywOS7tEBO/p.LS.RiTQderfDG6SZeDsq7RPvg6Mbwk.', 'Amministratore', 'admin'),
	(2, 'tecnico1@gmail.com', '$2a$10$/mRetNCuv6gKwPc5xk/50uJQLVHPIwRbEIoo.cP5tYoXAo3240ODG', 'Tecnico', 'tecnico1'),
	(4, 'tecnico2@gmail.com', '$2a$10$b5SQYJvB4XX7cl0uoEnQlOtdb0Uu/SHSVElHurJW.SkSB9DxU.wYS', 'Tecnico', 'tecnico2'),
	(33, 'luca@gmail.com', '$2a$10$HDgPmjBaJbQiXjMEEgGr5.18G74qTJ2/ZtAwRuc0ag80mn7pnbp5S', 'Ordinante', 'luca'),
	(34, 'pio@gmail.com', '$2a$10$fqzb8kFLL/8LXPweqeBH8OFVUBmL11MTh4evbLkKfPka8alEL4vHW', 'Ordinante', 'pio');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
