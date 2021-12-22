#include <iostream>
#include <fstream>
#include <cmath>
#include <cstring>
#define R 6371.004
#define SUP 100000005
using namespace std;
struct city{
    string name;
    double longitude;
    double latitude;
};
struct edge{
    int from;
    int to;
    double weight;
};
double rad(double angle){
    return angle * M_PI / 180;
}
//Only correct when the positions are on the north-eastern hemisphere
double earth_dist(city *pos_a, city *pos_b){
    double long_a = rad(pos_a->longitude);
    double lat_a = rad(pos_a->latitude);
    double long_b = rad(pos_b->longitude);
    double lat_b = rad(pos_b->latitude);
    double c = sin(lat_a) * sin(lat_b) + cos(lat_a) * cos(lat_b) * cos(long_b - long_a);
    return R * acos(c);
}
int main() {
    city c[35];
    edge chosen[35], low_cost[35];
    double graph[35][35] = {SUP};
    int visit[35] = {0}, flag = 0, n = 0, last_visit, cnt = 0;
    int hong_kong_idx, macao_idx, guang_zhou_idx;
    ifstream fin("position.txt");
    while (!fin.eof()){
        fin>>c[n].name>>c[n].longitude>>c[n].latitude;
        if (c[n].name == "香港") hong_kong_idx = n;
        else if (c[n].name == "澳门") macao_idx = n;
        else if (c[n].name == "广州市") guang_zhou_idx = n;
        n++;
    }
    //Build the graph
    for(int i = 0; i < n; i++)
        for(int j = 0; j < n; j++)
            if(i == j) graph[i][j] = 0;
            else {
                double dist = earth_dist(&c[i], &c[j]);
                graph[i][j] = dist;
                graph[j][i] = dist;
            }
    for(int i = 0; i < n; i++){
        low_cost[i] = {-1, -1, SUP};
    }
    visit[0] = 1;
    last_visit = 0;
    while (!flag){
        flag = 1;
        for(int i = 0; i < n; i++){
            if(visit[i] == 0 && graph[last_visit][i] < low_cost[i].weight){
                bool spec_1, spec_2;
                spec_1 = (last_visit == hong_kong_idx && i == macao_idx) || (last_visit == macao_idx && i == hong_kong_idx);
                spec_2 = (last_visit == macao_idx && i == guang_zhou_idx) || (last_visit == guang_zhou_idx && i == macao_idx);
                if (!spec_1 && !spec_2) {
                    low_cost[i].from = last_visit;
                    low_cost[i].to = i;
                    low_cost[i].weight = graph[last_visit][i];
                }
            }
        }
        int min_edge_idx = -1;
        double min_edge_dist = SUP;
        for (int i = 0; i < n; i++) {
            if(low_cost[i].weight != -1.0 && low_cost[i].weight < min_edge_dist){
                min_edge_dist = low_cost[i].weight;
                min_edge_idx = i;
            }
        }
        chosen[cnt] = low_cost[min_edge_idx];
        visit[min_edge_idx] = 1;
        low_cost[min_edge_idx] = {-1, -1, -1.0};
        last_visit = min_edge_idx;
        cnt++;
        for(int i = 0; i < n; i++){
            if(visit[i] == 0){
                flag = 0;
                break;
            }
        }
    }
    double sum = 0.0;
    for(int i = 0; i < cnt; i++){
        cout<<c[chosen[i].from].name<<"->"<<c[chosen[i].to].name<<" "<<chosen[i].weight<<"km"<<endl;
        sum += chosen[i].weight;
    }
    cout<<"总长度: "<<sum<<"km"<<endl;
    return 0;
}
