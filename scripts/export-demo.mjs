// Optional development utility: snapshot the sibling Angular project's fictitious seeds.
// The Java runtime never depends on Node or on the frontend repository.
import { readFile, writeFile, mkdir, mkdtemp, rm } from 'node:fs/promises';
import { resolve, dirname, join } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import { tmpdir } from 'node:os';
const backend = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const frontend = resolve(process.argv[2] || join(backend, '../FourMint'));
const ts = (await import(pathToFileURL(join(frontend, 'node_modules/typescript/lib/typescript.js')))).default;
const temporary = await mkdtemp(join(tmpdir(), 'fourmint-seed-export-'));
try {
  for (const name of ['seed', 'market-data', 'recon-seed', 'recon-engine']) {
    const source = await readFile(join(frontend, 'src/app/core', name + '.ts'), 'utf8');
    const output = ts.transpileModule(source, { compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.ES2022 } })
      .outputText.replace(/from '(\.\.?\/[^']+)'/g, "from '$1.mjs'");
    await writeFile(join(temporary, name + '.mjs'), output);
  }
  const core = (await import(pathToFileURL(join(temporary, 'seed.mjs')))).seed();
  const recon = (await import(pathToFileURL(join(temporary, 'recon-seed.mjs')))).reconSeed();
  const { runMatching } = await import(pathToFileURL(join(temporary, 'recon-engine.mjs')));
  const matching = runMatching(recon.records, new Set(), new Set(recon.files.map(file => file.source)));
  const at = core.processDate + 'T10:12:00';
  const results = matching.results.map((row, index) => ({
    ...row, id: 'MR-' + (index + 1), owner: '', escalated: false, created: at, updated: at, history: [], ai: [],
  }));
  const datasets = {
    'reference-service': {
      users: core.users, entities: core.entities, sdis: core.sdis, books: core.books, products: core.products,
      calendars: Object.entries(core.calendar).map(([id, holidays]) => ({ id, holidays })),
    },
    'trading-service': { trades: core.trades },
    'settlement-service': { transfers: core.transfers, messages: core.messages },
    'workflow-service': {
      tasks: core.tasks,
      'sla-weeks': core.sla.map(row => ({ id: row.week, ...row })),
      'support-cases': JSON.parse(await readFile(join(frontend, 'server/fixtures/salesforce-cases.json'), 'utf8')),
    },
    'market-data-service': {
      quotes: core.quotes,
      curves: core.curves.some(curve => curve.id === 'DI1') ? core.curves
        : [...core.curves, { id: 'DI1', label: 'DI1', unit: '%', snapshots: [{ date: core.processDate, points: core.di1 }] }],
    },
    'operations-service': {
      schedules: core.schedules, engines: core.engines,
      'business-contexts': [{ id: 'default', processDate: core.processDate, lockDate: core.lockDate, entityLabel: core.entityLabel }],
    },
    'reconciliation-service': {
      counterparties: recon.counterparties, 'internal-sources': [recon.calypso], records: recon.records, results,
      files: recon.files, 'pending-files': recon.pendingFiles,
      cycles: [{ id: recon.cycle.date, ...recon.cycle, progress: 100, lastRunAt: at, previous: recon.previous }],
      events: recon.events.map((event, index) => ({ id: 'RE-' + (index + 1), ...event })),
    },
  };
  for (const [service, data] of Object.entries(datasets)) {
    for (const [collection, rows] of Object.entries(data)) {
      if (new Set(rows.map(row => row.id)).size !== rows.length) throw new Error('Duplicate IDs in ' + service + '/' + collection);
    }
    const directory = join(backend, service, 'src/main/resources/demo');
    await mkdir(directory, { recursive: true });
    await writeFile(join(directory, 'seed.json'), JSON.stringify(data, null, 2) + '\n');
    console.log(service + ': ' + Object.values(data).reduce((count, rows) => count + rows.length, 0) + ' demo resources');
  }
} finally {
  await rm(temporary, { recursive: true, force: true });
}
