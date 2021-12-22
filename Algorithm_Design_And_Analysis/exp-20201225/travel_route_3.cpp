#include <iostream>
#include <fstream>
#include <cstring>
#include <stack>
#include <map>
#define SUP 100000005
using namespace std;
int graph[50][50], d[50], visit[50] = {0}, pred[50] = {0};
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
    stack<int> path_1, path_2;
    int tot_dist_1 = 0, tot_dist_2 = 0;
    map<string, int> location_index;
    map<int, string> inv_location_index;
    int n = 0, start, mid_1, mid_2, end;
    string start_city, mid_city_1, mid_city_2, end_city;
    for(int i = 0; i < 50; i++)
        for(int j = 0; j < 50; j++) {
            graph[i][j] = SUP;
            graph[j][i] = SUP;
        }
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
    start_city = "成都";
    mid_city_1 = "理塘";
    mid_city_2 = "道孚";
    end_city = "江达";
    start = location_index[start_city];
    mid_1 = location_index[mid_city_1];
    mid_2 = location_index[mid_city_2];
    end = location_index[end_city];
    //Find path 1
    find_path(mid_1, end, n);
    int cur = end;
    path_1.push(end);
    while(cur != mid_1){
        path_1.push(pred[cur]);
        cur = pred[cur];
    }
    tot_dist_1 += d[end];
    find_path(start, mid_1, n);
    cur = pred[mid_1];
    path_1.push(pred[mid_1]);
    while(cur != start){
        path_1.push(pred[cur]);
        cur = pred[cur];
    }
    tot_dist_1 += d[mid_1];
    //Find path 2
    find_path(mid_2, end, n);
    cur = end;
    path_2.push(end);
    while(cur != mid_2){
        path_2.push(pred[cur]);
        cur = pred[cur];
    }
    tot_dist_2 += d[end];
    find_path(start, mid_2, n);
    cur = pred[mid_2];
    path_2.push(pred[mid_2]);
    while(cur != start){
        path_2.push(pred[cur]);
        cur = pred[cur];
    }
    tot_dist_2 += d[mid_2];
    //Compare and print the smaller one
    if(tot_dist_1 <= tot_dist_2){
        cout<<"途径：理塘"<<endl;
        cout<<"路径：";
        while(!path_1.empty()){
            int p = path_1.top();
            cout<<inv_location_index[p]<<" ";
            path_1.pop();
        }
        cout<<endl;
        cout<<"Total: "<<tot_dist_1<<"km"<<endl;
    }
    else{
        cout<<"途径：道孚"<<endl;
        cout<<"路径：";
        while(!path_2.empty()){
            int p = path_2.top();
            cout<<inv_location_index[p]<<" ";
            path_2.pop();
        }
        cout<<endl;
        cout<<"Total: "<<tot_dist_2<<"km"<<endl;
    }
    fin.close();
    return 0;
}
