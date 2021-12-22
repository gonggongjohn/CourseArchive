#include<iostream>
#include<fstream>
#include<cstring>
#include<unordered_map>
using namespace std;
string a[10000005];
int main(){
    string flower[18] = {"paeoniaSA", "paeoniaLP", "camellia", "chrysanthemum", "plum", "orchid",
                         "rose", "azalea", "tulip", "jasmine", "begonia", "lotus",
                         "gardenia", "lotus", "lily", "carnation", "rose", "gesang"};
    //Simulate the description of flowers
    string description[18] = {"DpaeoniaSA", "DpaeoniaLP", "Dcamellia", "Dchrysanthemum", "Dplum", "Dorchid",
                              "Drose", "Dazalea", "Dtulip", "Djasmine", "Dbegonia", "Dlotus",
                              "Dgardenia", "Dlotus", "Dlily", "Dcarnation", "Drose", "Dgesang"};
    unordered_map<string, string> flower_map;
    for(int i = 0; i < 18; i++){
        flower_map[flower[i]] = description[i];
    }
    ifstream fin("data.txt");
    clock_t start, stop;
    int n = 0;
    while(!fin.eof()){
        fin>>a[n];
        n++;
    }
    n--;
    int cnt = 0;
    start = clock();
    for(int i = 0; i < n; i++){
        cnt++;
        string t = flower_map[a[i]];
        //cout<<t<<" ";
    }
    stop = clock();
    cout<<endl;
    cout<<"Total count: "<<cnt<<endl;
    cout<<"Total time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<"s"<<endl;
    return 0;
}