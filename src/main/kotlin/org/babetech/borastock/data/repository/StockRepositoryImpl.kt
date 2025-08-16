package org.babetech.borastock.data.repository

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.*
import org.babetech.borastock.domain.models.*
import org.babetech.borastock.domain.repository.StockRepository
import org.babetech.borastock.domain.repository.StockStat
import borastock.composeapp.generated.resources.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class StockRepositoryImpl : StockRepository {
    
    // États observables pour chaque type de données
    private val _stockItems = MutableStateFlow<List<StockItem>>(emptyList())
    override val stockItems: StateFlow<List<StockItem>> = _stockItems.asStateFlow()
    
    private val _stockEntries = MutableStateFlow<List<StockEntry>>(emptyList())
    override val stockEntries: StateFlow<List<StockEntry>> = _stockEntries.asStateFlow()
    
    private val _stockExits = MutableStateFlow<List<StockExit>>(emptyList())
    override val stockExits: StateFlow<List<StockExit>> = _stockExits.asStateFlow()
    
    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    override val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

    init {
        loadInitialData()
    }

    private fun nowMinusHours(hours: Int): LocalDateTime {
        return Clock.System.now()
            .minus(hours, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private fun loadInitialData() {
        _stockItems.value = listOf(
            StockItem(
                "1", "iPhone 15 Pro", "Électronique", 25, 10, 100, 1199.99,
                "Apple Inc.", "Il y a 2h", StockStatus.IN_STOCK
            ),
            StockItem(
                "2", "Samsung Galaxy S24", "Électronique", 8, 15, 80, 899.99,
                "Samsung", "Il y a 1h", StockStatus.LOW_STOCK
            ),
            StockItem(
                "3", "MacBook Air M3", "Informatique", 0, 5, 50, 1299.99,
                "Apple Inc.", "Il y a 30min", StockStatus.OUT_OF_STOCK
            ),
            StockItem(
                "4", "AirPods Pro", "Accessoires", 150, 20, 200, 249.99,
                "Apple Inc.", "Il y a 3h", StockStatus.OVERSTOCKED
            )
        )

        _stockEntries.value = listOf(
            StockEntry(
                "E001", "iPhone 15 Pro Max", "Électronique", 50, 1199.99, 59999.50,
                "Apple Inc.", nowMinusHours(2), "APL2024001", null,
                EntryStatus.RECEIVED, "Livraison conforme, emballage parfait"
            ),
            StockEntry(
                "E002", "Samsung Galaxy S24 Ultra", "Électronique", 30, 1299.99, 38999.70,
                "Samsung Electronics", nowMinusHours(4), "SAM2024002", null,
                EntryStatus.VALIDATED, "En attente de réception"
            )
        )

        _stockExits.value = listOf(
            StockExit(
                "S001", "iPhone 15 Pro Max", "Électronique", 2, 1199.99, 2399.98,
                "TechStore Paris", nowMinusHours(1), "CMD2024001",
                "123 Rue de Rivoli, 75001 Paris", ExitStatus.SHIPPED,
                "Livraison express demandée", ExitUrgency.HIGH
            ),
            StockExit(
                "S002", "Samsung Galaxy S24 Ultra", "Électronique", 1, 1299.99, 1299.99,
                "Mobile World Lyon", nowMinusHours(3), "CMD2024002",
                "45 Place Bellecour, 69002 Lyon", ExitStatus.DELIVERED,
                "Client satisfait, livraison réussie", ExitUrgency.LOW
            )
        )
    }

    // ===== STOCK ITEMS =====
    override suspend fun addStockItem(item: StockItem) {
        val currentList = _stockItems.value.toMutableList()
        currentList.add(0, item)
        _stockItems.value = currentList
    }

    override suspend fun updateStockItem(item: StockItem) {
        val currentList = _stockItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            currentList[index] = item
            _stockItems.value = currentList
        }
    }

    override suspend fun deleteStockItem(itemId: String) {
        val currentList = _stockItems.value.toMutableList()
        currentList.removeAll { it.id == itemId }
        _stockItems.value = currentList
    }

    // ===== STOCK ENTRIES =====
    override suspend fun addStockEntry(entry: StockEntry) {
        val currentList = _stockEntries.value.toMutableList()
        val newId = (currentList.maxOfOrNull { it.id.substring(1).toInt() } ?: 0) + 1
        val entryToAdd = entry.copy(
            id = "E${newId.toString().padStart(3, '0')}",
            entryDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            totalValue = entry.quantity * entry.unitPrice
        )
        currentList.add(0, entryToAdd)
        _stockEntries.value = currentList
    }

    override suspend fun updateStockEntry(entry: StockEntry) {
        val currentList = _stockEntries.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            currentList[index] = entry.copy(totalValue = entry.quantity * entry.unitPrice)
            _stockEntries.value = currentList
        }
    }

    override suspend fun deleteStockEntry(entryId: String) {
        val currentList = _stockEntries.value.toMutableList()
        currentList.removeAll { it.id == entryId }
        _stockEntries.value = currentList
    }

    // ===== STOCK EXITS =====
    override suspend fun addStockExit(exit: StockExit) {
        val currentList = _stockExits.value.toMutableList()
        val newId = (currentList.maxOfOrNull { it.id.substring(1).toInt() } ?: 0) + 1
        val exitToAdd = exit.copy(
            id = "S${newId.toString().padStart(3, '0')}",
            exitDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            totalValue = exit.quantity * exit.unitPrice
        )
        currentList.add(0, exitToAdd)
        _stockExits.value = currentList
    }

    override suspend fun updateStockExit(exit: StockExit) {
        val currentList = _stockExits.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == exit.id }
        if (index != -1) {
            currentList[index] = exit.copy(totalValue = exit.quantity * exit.unitPrice)
            _stockExits.value = currentList
        }
    }

    override suspend fun deleteStockExit(exitId: String) {
        val currentList = _stockExits.value.toMutableList()
        currentList.removeAll { it.id == exitId }
        _stockExits.value = currentList
    }

    // ===== SUPPLIERS =====
    override suspend fun addSupplier(supplier: Supplier) {
        val currentList = _suppliers.value.toMutableList()
        currentList.add(0, supplier)
        _suppliers.value = currentList
    }

    override suspend fun updateSupplier(supplier: Supplier) {
        val currentList = _suppliers.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == supplier.id }
        if (index != -1) {
            currentList[index] = supplier
            _suppliers.value = currentList
        }
    }

    override suspend fun deleteSupplier(supplierId: String) {
        val currentList = _suppliers.value.toMutableList()
        currentList.removeAll { it.id == supplierId }
        _suppliers.value = currentList
    }

    // ===== STATISTICS =====
    override fun getStockStatistics(): List<StockStat> {
        val items = _stockItems.value
        return listOf(
            StockStat("Total Produits", items.size.toString(), Res.drawable.inventory, Color(0xFF3b82f6)),
            StockStat("En Stock", items.count { it.status == StockStatus.IN_STOCK }.toString(), Res.drawable.CheckCircle, Color(0xFF22c55e)),
            StockStat("Stock Faible", items.count { it.status == StockStatus.LOW_STOCK }.toString(), Res.drawable.Warning, Color(0xFFf59e0b)),
            StockStat("Ruptures", items.count { it.status == StockStatus.OUT_OF_STOCK }.toString(), Res.drawable.Error, Color(0xFFef4444))
        )
    }

    override fun getEntryStatistics(): List<StockStat> {
        val entries = _stockEntries.value
        return listOf(
            StockStat("Total Entrées", entries.size.toString(), Res.drawable.Receipt, Color(0xFF3b82f6)),
            StockStat("En Attente", entries.count { it.status == EntryStatus.PENDING }.toString(), Res.drawable.Schedule, Color(0xFFf59e0b)),
            StockStat("Reçues", entries.count { it.status == EntryStatus.RECEIVED }.toString(), Res.drawable.CheckCircle, Color(0xFF22c55e))
        )
    }

    override fun getExitStatistics(): List<StockStat> {
        val exits = _stockExits.value
        return listOf(
            StockStat("Total Sorties", exits.size.toString(), Res.drawable.Receipt, Color(0xFF3b82f6)),
            StockStat("En Préparation", exits.count { it.status == ExitStatus.PENDING }.toString(), Res.drawable.Schedule, Color(0xFFf59e0b)),
            StockStat("Expédiées", exits.count { it.status == ExitStatus.SHIPPED }.toString(), Res.drawable.LocalShipping, Color(0xFF8b5cf6)),
            StockStat("Livrées", exits.count { it.status == ExitStatus.DELIVERED }.toString(), Res.drawable.ic_check_circle, Color(0xFF22c55e))
        )
    }
}