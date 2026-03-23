# Use Case E. Move objects
## Precondition
按鈕 select 被按下的情況。
## Definition
x, y 座標有可能落在某個物件的範圍內，這種情況該物件在 x, y 的座標上繪製會重疊其他物件。基本上物件重疊時，請按照物件深度的次序來繪製。
## Case
1. 使用者在編輯地區的某個物件（包含 composite 物件）範圍內按下 mouse 的左鍵，但是不放開(mouse pressed) 。
2. 使用者不放開左鍵，進行拖曳(drag)的動作。
3. 使用者拖曳到另外一個座標 x, y 放開左鍵 (mouse released) 。
4. 該物件被移動到新座標 x, y 。
5. 所有連結到該基本物件的 connection links 全部重新繪製。
