# Implementation Plan

## MVC 對應（目前程式結構）

本專案目前採用 MVC 分層，並已建立對應 package：

- **Model (`src/model`)**
  - `CanvasModel`：畫布資料的聚合入口（目前為骨架，後續承接 ObjectRegistry、LinkPool 等集合）。
  - `Vector2D`：座標值物件。
  - `shape/UMLNode`、`shape/UMLRect`、`shape/UMLOval`：節點資料模型（含 `UUID`、名稱、位置、尺寸）。
  - `enums/UserMode`：使用者模式列舉（Select / Link / Rect / Oval）。
- **View (`src/view`)**
  - `UMLPanel`：畫布顯示元件（Swing `JPanel`）。
  - `Main`：UI 組裝入口（工具列與畫布容器）。
- **Controller (`src/controller`)**
  - `UMLBase`：可被控制與繪製的基底抽象。
  - `CanvasManager`：互動狀態控制（目前持有 `currentMode`）。

## 基本物件定義（不變）

- 基本物件（Basic Object）：如 Rect、Oval。
- 連結物件（Link）：如 Association、Generalization、Composition。
- 群組物件（Composite）：由多個基本物件組成，可巢狀。
- 深度（Depth）：深度越小越在上層；上層物件優先攔截滑鼠事件；最後選取物件需提升至最上層。

## Model 資料結構規劃（對齊 MVC）

資料儲存責任收斂在 **Model 層**（由 `CanvasModel` 統一管理）：

- `ObjectRegistry`：以 UUID 為 key 管理畫布物件。
- `LinkPool`：集中管理所有 Link UUID。
- `depth` 用於繪製與 hit test 的優先序（越小越上層）。

Controller 僅負責操作 Model 與事件流程；View 只負責顯示與重繪觸發。

## Rendering（維持原策略，不調整）

每次重繪時維持既有三階段策略：

1. 先渲染 `LinkPool` 中「不含目前選取節點端點」的 Link。
2. 再依 `ObjectRegistry` 的 depth 順序，渲染所有「未被選取」的 Basic Object。
3. 最後渲染剩餘尚未渲染的 Basic Object 與 Link（包含目前操作中的物件與關聯連結）。

此策略可確保一般情況下 Object 不會被 Link 遮蓋，同時保留編輯中物件與其連結的可見性。
