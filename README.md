# Proto_Delivery_App Documentation

> **Disclaimer**:  
> This is a prototype for a deployed application. The final application is under NDA and cannot be shared. This prototype will not work properly without the back-end, the code for which is entirely the property of IdealRoofing Co.

## Table of Contents
1. [Login](#login)
2. [Home Screen](#home-screen)
3. [Orders](#orders)
4. [Order Details](#order-details)
5. [Scan Packs](#scan-packs)
6. [Submission](#submission)
7. [Background Services](#background-services)
8. [Synchronization](#synchronization)
9. [Dev Corner](#dev-corner)

---

## Login
The login screen is the entry point for drivers. Upon first launch, drivers select their preferred language, which can later be changed in the settings menu. 

- **API Endpoint**: `/get_driver/{token}&{phonenumber}`
- **Variables**:
  - `token`: Validates that the request originates from the app.
  - `phonenumber`: Used to fetch driver details from Epicor’s `drivers_list` BAQ.

**Note**: If driver phone numbers are added to the system later, this two-step process can be simplified.

---

## Home Screen
The home screen provides a calendar and a list of available delivery runs. Runs for a specific day are only loaded after that date is selected.

- **API Endpoint**: `/getrunperdriver/{token}&{id}&{reqdate}`
  - **id**: Driver's employee number.
  - **reqdate**: The selected date.

This API returns the runs for the selected day. After the runs are loaded, a secondary call fetches the orders for the run via `app_get_orders`.

---

## Orders
The order list view provides three options per order:
1. **Deliver Order**: Opens the pack scan screen.
2. **Details**: Shows the order's details.
3. **Map**: Opens Google Maps or Hammer maps (if offline) with the delivery address.

---

## Order Details
The details screen provides two views:
1. **Items**: Displays the items in the order.
2. **Packs**: Shows the packs for the order, including scanned status, and allows notes to be added.

---

## Scan Packs
Drivers start scanning packs on this screen. After scanning, they review the scanned packs and add notes if needed before proceeding
