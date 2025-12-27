package final


data class ShipmentItem(
    val productID: String,
    val quantity: Int,
    val weight: Double
)

data class Order(
    val customerName: String,
    val shipmentItems: List<ShipmentItem>
)

data class Product(
    val name: String,
    val shippingMethod: String
)

data class InvoiceData(val customerName: String, val loyaltyPoints: Int, val totalCost: Int)
data class InvoiceLine(val itemCost: Int, val itemQuantity: Int, val productName: String, val itemWeight: Double)
class InvoiceTextGenerator(val order: Order, val products: Map<String, Product>) {

    fun generate(): String {
        val invoiceData = InvoiceData(order.customerName, calcLoyaltyPoints(), calcTotalCost())
        var result = "Shipping Invoice for ${invoiceData.customerName}\n"
        for (item in order.shipmentItems) {
            val invoiceLine = getInvoiceLine(item)
            result += getInoviceForLineItem(invoiceLine)
        }

        result += "Total shipping cost is ${formatCurrency(calcTotalCost())}\n"
        result += "You earned ${invoiceData.loyaltyPoints} loyalty points\n"

        return result
    }

    private fun getInvoiceLine(item: ShipmentItem): InvoiceLine =
        InvoiceLine(calcItemCost(item), item.quantity, getProduct(item).name, item.weight)

    private fun getInoviceForLineItem(invoiceLine: InvoiceLine): String =
        "  ${invoiceLine.productName}: ${formatCurrency(invoiceLine.itemCost)} " +
                "(${invoiceLine.itemQuantity} items, ${
                    invoiceLine.itemWeight
                }kg)\n"

    private fun calcTotalCost(): Int {
        var totalCost = 0
        for (item in order.shipmentItems) {
            totalCost += calcItemCost(item)
        }
        return totalCost
    }

    private fun calcLoyaltyPoints(): Int {
        var loyaltyPoints = 0
        for (item in order.shipmentItems) {
            loyaltyPoints += calcLoyaltyPointsIncrease(item)
        }
        return loyaltyPoints
    }

    private fun getProduct(item: ShipmentItem): Product = products[item.productID]!!

    private fun calcLoyaltyPointsIncrease(item: ShipmentItem): Int {
        val product = getProduct(item)
        var loyaltyPoints = maxOf(item.quantity - 2, 0)
        if ("express" == product.shippingMethod) {
            loyaltyPoints += item.quantity / 3
        }
        return loyaltyPoints
    }

    private fun calcItemCost(item: ShipmentItem): Int {
        val product = getProduct(item)
        var itemCost = 0
        when (product.shippingMethod) {
            "standard" -> {
                itemCost = 500
                if (item.weight > 5.0) {
                    itemCost += (100 * (item.weight - 5.0)).toInt()
                }
            }

            "express" -> {
                itemCost = 1200
                if (item.weight > 3.0) {
                    itemCost += (250 * (item.weight - 3.0)).toInt()
                }

                itemCost += 150 * item.quantity
            }

            else -> {
                throw kotlin.IllegalArgumentException("unknown shipping method: ${product.shippingMethod}")
            }
        }
        return itemCost
    }

    fun formatCurrency(amountInCents: Int): String {
        val amountInDollars = amountInCents / 100.0
        return "$%.2f".format(amountInDollars)
    }
}


