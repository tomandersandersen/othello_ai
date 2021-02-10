clear;
clc;
close all;

DATA = readmatrix('log1.txt');
a1 = DATA(:,1);
b1 = DATA(:,2);
histplotter(a1, b1, "H_1");

DATA = readmatrix('log2.txt');
a2 = DATA(:,1);
b2 = DATA(:,2);
histplotter(a2, b2, "H_2");

DATA = readmatrix('log3.txt');
a3 = DATA(:,1);
b3 = DATA(:,2);
histplotter(a3, b3, "H_3");


figure; blim = 5;
subplot(311);
histogram(a1, 30); hold on; grid on;
ylim([0 blim]);
xlim([0 64]);
title("H_1")
subplot(312);
histogram(a2, 30); hold on; grid on;
ylim([0 blim]);
xlim([0 64]);
title("H_2")
subplot(313);
histogram(a3, 30); hold on; grid on;
ylim([0 blim]);
xlim([0 64]);
title("H_3")
sgtitle("Histogram of score for 100 games for different heuristics.")

figure;
bar([win_counter(a1, b1), win_counter(a2, b2), win_counter(a3, b3)] ./20);
hold on; grid on;
ylim([0 1])
title("Win count");
set(gca,'xticklabel',{'H_1', 'H_2', 'H_3'})


function Y =  win_counter(a, b)
    Y = sum(a>b);
end

function histplotter(a, b, NAME)
blim = 5;
figure;
subplot(211)
histogram(a, 30); hold on; grid on;
ylim([0 blim]);
xlim([0 64]);
legend("Minmax AI")
subplot(212)
histogram(b, 30, 'FaceColor', 'red'); hold on; grid on;
ylim([0 blim]);
xlim([0 64])
ylabel("Occurrence")
xlabel("Score")
sgtitle("100 games histogram using heuristics " + NAME)
legend("Random")
end
