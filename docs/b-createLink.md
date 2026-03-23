# Use Case B. Creating a Link
## Precondition
適用 Association, Generalization 以及 Composition 三個按鈕任一被按下的情況。
## Definition
Connection link 的建立是連結在基本物件的 ports 上。 基本物件 ，如下圖 Rect 和 Oval 分別
有 8 和 4 個 ports。

## Case
1. 使用者在編輯地區的某個 Rect 或 Oval 物件的任一 port 的範圍內按下 mouse 的左鍵，但是不放開(mouse pressed)。
2. 使用者不放開左鍵，進行拖曳(drag)的動作。
3. 使用者拖曳到另外一個 Rect 或 Oval 物件的任一 port 的範圍內，放開左鍵 (mouse released) 。
4. 在編輯區內，建立一個 link 的物件。連接兩個物件。依照 connection link 的種類，將各種箭頭繪製於終點的物件。
### Alternatives B.1
使用者 mouse pressed 的座標，不在任何 Rect 或 Oval 物件，則從 mouse pressed -> mouse
drag -> mouse released 都不會有任何作用。
### Alternatives B.2
使用者 mouse released 的座標，不在任何 Rect 或 Oval 物件（或與 mouse pressed 座標屬於
同一個物件），則不建立任何 connection link 物件。
> 也就是說，當使用者於步驟 1 或步驟 3 按下或放開 mouse 左鍵時，請判斷該座標位於哪個基本物件的 port 的範圍內。請注意，本規則不適用 composite 物件。
