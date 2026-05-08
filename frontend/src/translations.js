<<<<<<< HEAD
const dictionary = {
=======
export const dictionary = {
>>>>>>> d57551ef73b29f3127b61637c07b2ad1e772d986
  EquipmentID: "ID do Equipamento",
  EquipmentName: "Nome do Equipamento",
  MaterialID: "ID do Material",
  MaterialName: "Nome do Material",
  PurchaseOrderID: "ID do Pedido de Compra",
  DeliveryDate: "Data de Entrega",
  Supplier: "Fornecedor",
  Quantity: "Quantidade",
  TotalCost: "Custo Total",
  SalesOrderID: "ID do Pedido de Venda",
  Customer: "Cliente",
  TotalValue: "Valor Total",
  WorkforceID: "ID do Funcionário",
  Name: "Nome",
  Shift: "Turno"
};

export function translateKey(key) {
  return dictionary[key] || key;
}
