package compiler;

import java.util.*;

//4
//w1#s1
//w2#s2
//w3#s3
//w4#s1
//10
//s1#40#10#101
//s2#10#5#102
//s3#90#15#103
//s3#91#20#104
//s2#20#5#105
//s1#20#10#106
//s1#90#15#107
//s2#30#20#108
//s3#40#5#109
//s1#50#5#110

//4           
//w1#s1       
//w2#s2       
//w3#s3       
//w4#s1 
//4
//s1#20#5#1001
//s1#20#4#1002
//s3#13#6#1003
//s2#15#8#1007

//4    
//w1#s1
//w2#s2
//w3#s3
//w4#s1
//4
//s1#20#5#1001
//s1#20#4#1002
//s1#20#10#1004
//s3#12#6#1005

class Task {
	String skill;
	int prior;
	int time;
	int id;

	public Task(String skill, int prior, int time, int id) {
		this.skill = skill;
		this.prior = prior;
		this.time = time;
		this.id = id;
	}
}

class Worker {
	int occ;
	String worker;
	String op;

	public Worker(String worker) {
		this.worker = worker;
		op = worker;
	}

}

class Mapper {
	List<Worker> inWorkWorker;
	PriorityQueue<Worker> wrkrQueue;

	public Mapper(List<Worker> inWorkWorker, PriorityQueue<Worker> wrkrQueue) {
		this.inWorkWorker = inWorkWorker;
		this.wrkrQueue = wrkrQueue;
	}
}

public class Test {
	Comparator<Worker> wkrPriorComparator;
	Comparator<Task> taskPQ;

	PriorityQueue<Task> tPQ;
	Map<String, Mapper> skillQueueMapper;

	PriorityQueue<Worker> opPQ;

	static Scanner sc;

	Test() {
		sc = new Scanner(System.in);

		wkrPriorComparator = (a, b) -> a.worker.charAt(1) - b.worker.charAt(1);
		taskPQ = (a, b) -> {
			int x = b.prior - a.prior;
			if(x == 0) {
				return a.time - b.time;
			}
			return x;
		};

		tPQ = new PriorityQueue<>(taskPQ);

		skillQueueMapper = new HashMap<>();

		opPQ = new PriorityQueue<>(wkrPriorComparator);

	}

	void inpWorker() {
		int n = sc.nextInt();
		List<String> workerL = new ArrayList<>();

		for (int i = 0; i < n; i++) {
			workerL.add(sc.next());
			List<String> tempL = Arrays.asList(workerL.get(i).split("#"));

			Worker w1 = new Worker(tempL.get(0));

			PriorityQueue<Worker> pq;

			if (!skillQueueMapper.containsKey(tempL.get(1))) {
				skillQueueMapper.put(tempL.get(1), new Mapper(getList(), pq = new PriorityQueue<>(wkrPriorComparator)));
			} else {
				pq = skillQueueMapper.get(tempL.get(1)).wrkrQueue;
			}

			pq.add(w1);
		}
	}

	void inpTask() {
		int n = sc.nextInt();

		for (int i = 0; i < n; i++) {
			String inp = sc.next();
			List<String> tempL = Arrays.asList(inp.split("#"));

			Task task = new Task(tempL.get(0), Integer.parseInt(tempL.get(1)), Integer.parseInt(tempL.get(2)),
					Integer.parseInt(tempL.get(3)));

			tPQ.add(task);
		}
	}

	void op() {
		while (!opPQ.isEmpty()) {
			System.out.println(opPQ.poll().op);
		}
	}

	@SuppressWarnings("serial")
	List<Worker> getList() {
		return new ArrayList<Worker>() {
			public boolean add(Worker e) {
				super.add(e);
				Collections.sort(this, (a, b) -> a.occ - b.occ);
				return false;
			}
		};
	}

	void processTask() {
		Task task;

		while ((task = tPQ.poll()) != null) {

			final Mapper mapper = skillQueueMapper.get(task.skill);
			Worker worker = mapper.wrkrQueue.peek();
			Worker worker1, worker2;

			if (worker == null) {
				worker = mapper.inWorkWorker.remove(0);
				worker1 = worker;
				mapper.inWorkWorker.add(worker);
				mapper.inWorkWorker.stream().forEach(e -> e.occ -= worker1.occ);

				worker.occ = 0;
			}

			worker2 = worker;
			mapper.inWorkWorker.stream().forEach(e -> e.occ -= worker2.occ);
			worker.occ = task.time;
			mapper.inWorkWorker.add(worker);
			mapper.wrkrQueue.poll();
			worker.op += "#" + task.id;
			if (!opPQ.contains(worker))
				opPQ.add(worker);
			
			mapper.inWorkWorker.removeIf(e -> {
				if (e.occ < 0) {
					e.occ = 0;
					mapper.wrkrQueue.add(e);
					return true;
				}
				return false;
			});
		}
	}

	public static void main(String[] args) {
		Test test = new Test();
		test.inpWorker();
		test.inpTask();
		test.processTask();
		test.op();
	}
}
