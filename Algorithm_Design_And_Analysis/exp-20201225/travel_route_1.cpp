#include <iostream>
#include <fstream>
#include <stack>
#include <cstring>
#include <map>
#define SUP 100000005
using namespace std;
int graph[50][50], d[50], visit[50] = {0}, pred[50] = {0};
//Procedure of finding the shortest path
void find_path(int from, int to, int n){
    int flag = 0;
    for(int i = 0; i < n; i++) d[i] = SUP;
    for(int i = 0; i < n; i++) visit[i] = 0;
    d[from] = 0;
    while (!flag){
        int min_dist = SUP, min_idx = -1;
        for(int i = 0; i < n; i++){
            if (visit[i] == 0 && d[i] < min_dist){
                min_dist = d[i];
                min_idx = i;
            }
        }
        visit[min_idx] = 1;
        if(min_idx == to) break;
        for(int i = 0; i < n; i++){
            if (visit[i] == 0 && graph[min_idx][i] != SUP){
                if(d[i] > d[min_idx] + graph[min_idx][i]) {
                    d[i] = d[min_idx] + graph[min_idx][i];
                    pred[i] = min_idx;
                }
            }
        }
        flag = 1;
        for (int i = 0; i < n; i++) {
            if (visit[i] == 0){
                flag = 0;
                break;
            }
        }
    }
}
int main() {
    ifstream fin("data.txt");
    stack<int> path;
    map<string, int> location_index;
    map<int, string> inv_location_index;
    int n = 0, start, end, flag = 0;
    string start_city, end_city;
    for(int i = 0; i < 50; i++)
        for(int j = 0; j < 50; j++) {
            graph[i][j] = SUP;
            graph[j][i] = SUP;
        }
    //Read in data & build the graph
    while(!fin.eof()){
        string from, to;
        int from_index, to_index, distance, speed;
        fin>>from>>to>>distance>>speed;
        if (location_index.find(from) == location_index.end()){
            from_index = n;
            location_index[from] = from_index;
            inv_location_index[from_index] = from;
            n++;
        }
        else{
            from_index = location_index[from];
        }
        if (location_index.find(to) == location_index.end()){
            to_index = n;
            location_index[to] = to_index;
            inv_location_index[to_index] = to;
            n++;
        }
        else{
            to_index = location_index[to];
        }
        graph[from_index][to_index] = distance;
        graph[to_index][from_index] = distance;
    }
    start_city = "??????";
    end_city = "??????";
    start = location_index[start_city];
    end = location_index[end_city];
    find_path(start, end, n);
    //Recall the path
    int cur = end;
    path.push(end);
    while(cur != start){
        path.push(pred[cur]);
        cur = pred[cur];
    }
    cout<<"?????????";
    while(!path.empty()) {
        cout << inv_location_index[path.top()] << " ";
        path.pop();
    }
    cout<<endl;
    cout<<"Total: "<<d[end]<<"km"<<endl;
    fin.close();
    return 0;
}
