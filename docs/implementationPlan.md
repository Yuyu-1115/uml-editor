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

## Structure
在 Canvas 中，所有物件會儲存在一個以UUID為key的HashMap中（ObjectRegistry），depth 越低的物件在樹中會更高。即從root node開始進行 BFS 的話，越上層的物件會越早被 traversed 到。
除此之外，所有Link的UUID都會另外被儲存在一個稱作 LinkPool 的Set裡面。

## Classes
所有存在於Canvas中的物件都屬於一個 `UMLBase` 物件，`UMLBase`承載了基本的介面以及屬性：
```
int depth
bool isSelected
public abstract void draw(Graphics2D graphics2D)
```


## Rendering
物件的渲染會採用類似 Flutter 的 Widget Tree 的概念，每次需要重新渲染畫面時，會執行以下步驟：
1. 從LinkPool中依序渲染所有 endpoint 不包含目前所選擇的 Basic Object/Composite 的Link。
2. 從ObjectRegistry中依序（Depth）將所有目前沒有被選擇的 Basic Object 渲染至畫面上。
3. 最後將所有未渲染的 Basic Object 以及 Link 渲染至畫面上。

有鑒於 Object 幾乎不應該被 Link 蓋住，這種渲染方式能確保只有正在編輯的物件以及相關的連結能夠被清楚地看見。
