# Use Case C. Select / Unselect a single object
## Precondition
適用按鈕 select 被按下的情況。
## Definition
當一個基本物件被處於被 select/hover 的狀態，我們會將所有 ports 明確顯示出來，以表示基本物件處於被 select 的狀態。相反的若基本物件處於不被 select/hover 的狀態，則ports 是隱藏的（如果是 composite 物件則是僅顯示組合物件外框）。

## Case 1
1. 使用者將滑鼠移動至某物件範圍內。
2. 將此物件的 ports (或外框) 做明確的顯示。
3. 使用者點選該物件。
4. 若有其他物件處於被 select 的狀態，取消它們被 select 的狀態。
### Alternatives C.1
使用者點選的座標，不在任何基本物件內則不會有任何作用。
### Alternatives C.2
若有其他物件處於被 select 的狀態，取消它們被 select 的狀態。
## Case 2
1. 使用者在編輯區座標 x1, y1 按住 mouse 左鍵不放，x1, y1 不屬於任何基本物件的範圍內。
2. 4. 5. 若原本其他物件處於被 select 的狀態，取消它們被 select 的狀態。
3. 使用者不放開左鍵，進行拖曳(drag)的動作。使用者拖曳到另外一個座標 x2, y2，放開左鍵 (mouse released) 。(x1, y1, x2, y2) 形成一個四方形的區域。在該區域內的基本物件若完全落於此四方形區域，則處於被 select 的狀態 。
### Alternatives C.3
(x1, y1, x2, y2) 形成一個四方形的區域。在該區域內的沒有基本物件完全落於此四方形區 域。則本情境等於 unselect 所有之前處於被 select 狀態的物件。
