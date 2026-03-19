# Implementation Plan
## Basic Definition
- 基本物件(Basic Object)：如 Rect 或 Oval 物件。
- 連結物件(Link)：如各種 Association、Generalization、Composition Links。
- 群組物件(Composite)：Composite 物件由多個基本物件經過 Group 的功能組合而
成。Composite 物件是一種樹狀的 container，也就是說 composite 物件本身又可以
包含 composite 物件。composite 物件的範圍可以定義為最小的正方形區域完全包
含它的所有組成物件。
- 物件深度(depth)：每個物件相對於其他的物件都有一個深度值 0-99，若某個物件
的深度值比其他物件深度值相對少，在繪圖時，該物件應該覆蓋其他物件，而且先
接收與攔截落於該物件的 mouse 事件。也就是說，當兩個物件重疊時有 mouse 事
件被觸發，則只有最上層的物件會接收到該 mouse 事件。另外，最後選取的物件
應該被繪製於最上層。換言之，最後選取的物件的深度值應該調整為最小。
